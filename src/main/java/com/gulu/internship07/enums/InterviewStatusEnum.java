package com.gulu.internship07.enums;

import lombok.Getter;

@Getter
public enum InterviewStatusEnum {
    PENDING(0, "待面试", "orange", "fa-calendar"),
    COMPLETED(1, "面试完成", "green", "fa-check"),
    CANCELLED(2, "面试取消", "gray", "fa-ban");

    private final int code;
    private final String text;
    private final String color;
    private final String icon;

    InterviewStatusEnum(int code, String text, String color, String icon) {
        this.code = code;
        this.text = text;
        this.color = color;
        this.icon = icon;
    }
} 