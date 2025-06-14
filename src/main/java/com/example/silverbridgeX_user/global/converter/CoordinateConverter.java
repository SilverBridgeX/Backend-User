package com.example.silverbridgeX_user.global.converter;

import com.example.silverbridgeX_user.global.dto.CoordinateDto.simpleCoordinateDto;

public class CoordinateConverter {

    public static simpleCoordinateDto simpleCoordinateDto(String x, String y) {

        return simpleCoordinateDto.builder()
                .x(x)
                .y(y)
                .build();
    }
}
