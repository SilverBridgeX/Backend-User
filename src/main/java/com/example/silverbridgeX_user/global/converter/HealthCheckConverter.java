package com.example.silverbridgeX_user.global.converter;

import com.example.silverbridgeX_user.global.dto.HealthCheckDTO;

public class HealthCheckConverter {

    public static HealthCheckDTO.MessageDTO toHealthCheckDTO(String message) {
        return HealthCheckDTO.MessageDTO
                .builder()
                .message(message)
                .build();
    }
}
