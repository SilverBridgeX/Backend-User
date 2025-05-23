package com.example.silverbridgeX_user.global.dto;

import lombok.*;

public class HealthCheckDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MessageDTO {
        private String message;
    }
}
