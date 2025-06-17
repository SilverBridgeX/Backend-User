package com.example.silverbridgeX_user.user.jwt;


import com.example.silverbridgeX_user.user.dto.JwtDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
// JWT 관련 기능들을 넣어두기 위한 기능성 클래스
public class JwtTokenUtils {
    private final Key signingKey;
    private final JwtParser jwtParser;
    private final int accessExpirationTime;
    private final int refreshExpirationTime;

    public JwtTokenUtils(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.accessExpirationTime}") int accessExpirationTime,
            @Value("${jwt.refreshExpirationTime}") int refreshExpirationTime
    ) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts.parserBuilder().setSigningKey(this.signingKey).build();
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    // 주어진 사용자 정보를 바탕으로 JWT를 문자열로 생성
    public JwtDto generateToken(UserDetails userDetails) {
        log.info("\"{}\" jwt 발급", userDetails.getUsername());
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        // Claims: JWT에 담기는 정보의 단위를 Claim이라 부른다.
        //         Claims는 Claim들을 담기위한 Map의 상속 interface
        Claims accessTokenClaims = Jwts.claims()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(java.sql.Date.from(Instant.now()))
                .setExpiration(java.sql.Date.from(Instant.now().plusSeconds(accessExpirationTime)));
        String accessToken = Jwts.builder()
                .setClaims(accessTokenClaims)
                .claim("authorities", authorities)
                .signWith(signingKey)
                .compact();

        Claims refreshTokenClaims = Jwts.claims()
                .setIssuedAt(java.sql.Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(refreshExpirationTime)));
        String refreshToken = Jwts.builder()
                .setClaims(refreshTokenClaims)
                .claim("authorities", authorities)
                .signWith(signingKey)
                .compact();

        return JwtDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // JWT를 인자로 받고, 그 JWT를 해석해서
    // 사용자 정보를 회수하는 메소드
    public Claims parseClaims(String token) {
        log.info("jwt parsing : {}", jwtParser.parseClaimsJws(token).getBody());
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromToken(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);  // 토큰을 파싱해서 예외가 발생하지 않으면 유효한 토큰
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            // 잘못된 JWT 서명
            System.out.println("잘못된 JWT 서명");
        } catch (ExpiredJwtException e) {
            // 만료된 JWT
            System.out.println("JWT 토큰이 만료됨");
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT
            System.out.println("지원되지 않는 JWT 토큰");
        } catch (IllegalArgumentException e) {
            // 잘못된 토큰
            System.out.println("잘못된 JWT 토큰");
        }
        return false;  // 검증 실패
    }

    // 문자열로 저장된 authorities를 다시 Collection으로 변환
    public Collection<? extends GrantedAuthority> getAuthFromClaims(Claims claims) {

        String authoritiesString = (String) claims.get("authorities"); // authorities 정보 가져오기

        return Arrays.stream(authoritiesString.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
