package com.example.silverbridgeX_user.global.controller;

import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.jwt.CustomUserDetails;
import com.example.silverbridgeX_user.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/auth/verify")
    public ResponseEntity<?> verifyToken(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());

        String userId = String.valueOf(user.getId());

        return ResponseEntity.ok()
                .header("X-User-Id", userId)
                .body("OK");
    }
}
