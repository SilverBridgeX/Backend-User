package com.example.silverbridgeX_user.activity.service;

import com.example.silverbridgeX_user.activity.converter.ActivityLogConverter;
import com.example.silverbridgeX_user.activity.domain.Activity;
import com.example.silverbridgeX_user.activity.domain.ActivityLog;
import com.example.silverbridgeX_user.activity.repository.ActivityLogRepository;
import com.example.silverbridgeX_user.activity.repository.ActivityRepository;
import com.example.silverbridgeX_user.global.api_payload.ErrorCode;
import com.example.silverbridgeX_user.global.exception.GeneralException;
import com.example.silverbridgeX_user.user.converter.UserConverter;
import com.example.silverbridgeX_user.user.domain.User;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {




}
