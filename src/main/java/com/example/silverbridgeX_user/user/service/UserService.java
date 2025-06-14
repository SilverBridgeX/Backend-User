package com.example.silverbridgeX_user.user.service;

import com.example.silverbridgeX_user.global.api_payload.ErrorCode;
import com.example.silverbridgeX_user.global.dto.CoordinateDto;
import com.example.silverbridgeX_user.global.exception.GeneralException;
import com.example.silverbridgeX_user.global.service.CoordinateService;
import com.example.silverbridgeX_user.global.util.UUID;
import com.example.silverbridgeX_user.user.converter.UserConverter;
import com.example.silverbridgeX_user.user.domain.RefreshToken;
import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.domain.UserRole;
import com.example.silverbridgeX_user.user.dto.JwtDto;
import com.example.silverbridgeX_user.user.dto.UserRequestDto;
import com.example.silverbridgeX_user.user.dto.UserRequestDto.UserPreferenceDto;
import com.example.silverbridgeX_user.user.dto.UserRequestDto.UserReqDto;
import com.example.silverbridgeX_user.user.jwt.JwtTokenUtils;
import com.example.silverbridgeX_user.user.repository.RefreshTokenRepository;
import com.example.silverbridgeX_user.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JpaUserDetailsManager manager;
    private final CoordinateService coordinateService;
    private final JwtTokenUtils jwtTokenUtils;
    private final RestTemplate restTemplate;
    private final Driver driver;

    @Value("${chat.server.url}")
    private String chatServerUrl;

    private String PREF_URL;

    @PostConstruct
    public void init() {
        this.PREF_URL = chatServerUrl + "/summary/preferences/all";
    }

    @Transactional
    public User findByUserName(String userName) {
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND_BY_USERNAME));
    }

    @Transactional
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND_BY_EMAIL));
    }

    @Transactional
    public Boolean checkMemberByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User createUser(UserReqDto userReqDto) throws Exception {

        String uniqueKey;
        do {
            uniqueKey = UUID.generate();
        } while (userRepository.existsByUsername(uniqueKey));

        User newUser = UserConverter.saveUser(userReqDto, uniqueKey);
        userRepository.save(newUser);

        if (userReqDto.getRole().equals(UserRole.OLDER)) {

            insertUserNodeIfNotExists(newUser.getId());

            CoordinateDto.simpleCoordinateDto coordinateDto =
                    coordinateService.getCoordinateByAddress("ROAD", userReqDto.getStreetAddress());

            newUser.updateCoordinate(coordinateDto.getX(), coordinateDto.getY());
            userRepository.save(newUser);
        }

        manager.loadUserByUsername(newUser.getUsername()); // 저장된 사용자 정보를 다시 로드하여 동기화 시도
        return newUser;
    }

    @Transactional
    public JwtDto jwtMakeSave(String username) {
        UserDetails details
                = manager.loadUserByUsername(username);

        JwtDto jwt = jwtTokenUtils.generateToken(details);
        log.info("accessToken: {}", jwt.getAccessToken());
        log.info("refreshToken: {} ", jwt.getRefreshToken());

        // 리프레시 토큰 업데이트
        Optional<RefreshToken> existingToken = refreshTokenRepository.findById(username);
        if (existingToken.isPresent()) {
            refreshTokenRepository.deleteById(username);
        }
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .username(username)
                        .refreshToken(jwt.getRefreshToken())
                        .build()
        );

        return jwt;
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").split(" ")[1];

        String username = jwtTokenUtils.parseClaims(accessToken).getSubject();
        log.info("access token에서 추출한 username : {}", username);
        if (refreshTokenRepository.existsByUsername(username)) {
            refreshTokenRepository.deleteByUsername(username);
            log.info("DB에서 리프레시 토큰 삭제 완료");
        } else {
            throw GeneralException.of(ErrorCode.WRONG_REFRESH_TOKEN);
        }
    }

    @Transactional
    public JwtDto reissue(HttpServletRequest request) {
        // 1. Request에서 Refresh Token 추출
        String refreshTokenValue = request.getHeader("Authorization").split(" ")[1];

        // 2. DB에서 해당 Refresh Token을 찾음
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenValue)
                .orElseThrow(() -> new GeneralException(ErrorCode.WRONG_REFRESH_TOKEN));
        log.info("찾은 refresh token : {}", refreshToken);

        // 3. Refresh Token을 발급한 사용자 정보 로드
        UserDetails userDetails = manager.loadUserByUsername(refreshToken.getUsername());
        log.info("refresh token에서 추출한 username : {}", refreshToken.getUsername());

        // 4. 새로운 Access Token 및 Refresh Token 생성, 저장
        JwtDto jwt = jwtTokenUtils.generateToken(userDetails);
        log.info("reissue: refresh token 재발급 완료");
        refreshToken.updateRefreshToken(jwt.getRefreshToken());

        log.info("accessToken: {}", jwt.getAccessToken());
        log.info("refreshToken: {} ", jwt.getRefreshToken());

        // 5. DB에 새로운 리프레시 토큰이 정상적으로 저장되었는지 확인
        if (!refreshTokenRepository.existsByUsername(refreshToken.getUsername())) {
            throw GeneralException.of(ErrorCode.WRONG_REFRESH_TOKEN);
        }

        return jwt;
    }

    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        if (refreshTokenRepository.existsByUsername(username)) {
            refreshTokenRepository.deleteByUsername(username);
            log.info("DB에서 리프레시 토큰 삭제 완료");
        }

        if (user.getRole().equals(UserRole.PROTECTOR)) {
            for (User older : user.getOlders()) {
                older.updateProtector(null); // FK 끊기
            }
        }

        userRepository.delete(user);
        log.info("{} 회원 탈퇴 완료", username);
    }

    public void insertUserNodeIfNotExists(Long userId) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                // 존재 여부 확인
                var result = tx.run(
                        "MATCH (u:User {id: $id}) RETURN count(u) > 0 AS exists",
                        Map.of("id", userId)
                );
                boolean exists = result.single().get("exists").asBoolean();

                if (!exists) {
                    tx.run("""
                                CREATE (u:User {
                                    id: $id
                                })
                            """, Map.of(
                            "id", userId
                    ));
                    log.info("사용자 노드 생성 완료: " + userId);
                } else {
                    log.info("이미 존재: " + userId);
                }
                return null;
            });
        }
    }

    @Transactional
    public void saveNickname(UserRequestDto.UserNicknameReqDto nicknameReqDto, User user) {
        String nickname = nicknameReqDto.getNickname();
        user.updateNickname(nickname);
        userRepository.save(user);
    }

    @Transactional
    public void saveAddress(UserRequestDto.UserAddressReqDto addressReqDto, User user) throws Exception {
        String address = addressReqDto.getStreetAddress();
        user.updateAddress(address);

        CoordinateDto.simpleCoordinateDto coordinateDto = coordinateService.getCoordinateByAddress("ROAD", address);
        user.updateCoordinate(coordinateDto.getX(), coordinateDto.getY());
        userRepository.save(user);
    }

    @Transactional
    public void updatePreferredKeywords() {
        ResponseEntity<UserPreferenceDto[]> response
                = restTemplate.getForEntity(PREF_URL, UserPreferenceDto[].class);
        UserPreferenceDto[] dtos = response.getBody();

        if (dtos == null) {
            return;
        }

        for (UserPreferenceDto dto : dtos) {
            userRepository.findById(dto.getUserId()).ifPresent(user -> {
                List<String> deduplicated = dto.getPreferences().stream()
                        .distinct()
                        .toList();

                user.updatePreferredKeywords(deduplicated);
                userRepository.save(user);
                log.info(user.toString());
            });
        }
    }

    public void validateOlder(User user) {
        if (!user.getRole().equals(UserRole.OLDER)) {
            log.info(String.valueOf(user.getRole()));
            throw new GeneralException(ErrorCode.USER_NOT_OLDER);
        }
    }

    public void validateProtector(User user) {
        if (!user.getRole().equals(UserRole.PROTECTOR)) {
            log.info(String.valueOf(user.getRole()));
            throw new GeneralException(ErrorCode.USER_NOT_PROTECTOR);
        }
    }

    @Transactional
    public void connectOlder(User protector, String key) {
        User older = userRepository.findByUsername(key)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND_BY_USERNAME));

        validateProtector(protector);
        validateOlder(older);

        older.updateProtector(protector);
        userRepository.save(older);
    }

    @Transactional
    public void registerOlder(User protector, User older) {
        validateProtector(protector);
        validateOlder(older);

        older.updateProtector(protector);
        userRepository.save(older);
    }

}
