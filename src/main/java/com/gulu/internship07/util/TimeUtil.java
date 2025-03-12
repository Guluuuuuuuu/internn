package com.gulu.internship07.util;

import java.time.LocalDateTime;
import java.time.Duration;

public class TimeUtil {
    public static String getTimeRemaining(LocalDateTime deadline) {
        if (deadline == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, deadline);
        
        if (duration.isNegative()) return "已过期";
        
        long hours = duration.toHours();
        if (hours < 24) return String.format("%d小时", hours);
        
        long days = duration.toDays();
        return String.format("%d天", days);
    }
} 