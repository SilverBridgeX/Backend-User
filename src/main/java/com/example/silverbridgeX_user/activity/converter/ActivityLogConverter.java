package com.example.silverbridgeX_user.activity.converter;

import com.example.silverbridgeX_user.activity.domain.ActionType;
import com.example.silverbridgeX_user.activity.domain.Activity;
import com.example.silverbridgeX_user.activity.domain.ActivityLog;
import com.example.silverbridgeX_user.user.domain.User;
import java.time.LocalDateTime;
import java.util.Objects;

public class ActivityLogConverter {
    public static ActivityLog saveActivityLog(User user, Activity activity, LocalDateTime now, String type) {
        ActionType actionType = null;
        if (Objects.equals(type, "View")) {
            actionType = ActionType.VIEW;
        } else if (Objects.equals(type, "SELECT")) {
            actionType = ActionType.SELECT;
        }

        return ActivityLog.builder()
                .actionType(actionType)
                .time(now)
                .user(user)
                .activity(activity)
                .build();
    }
}
