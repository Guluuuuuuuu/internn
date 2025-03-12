package com.gulu.internship07.enums;

import lombok.Getter;

@Getter
public enum ApplicationStatusEnum {
    // 申请主状态
    SUBMITTED(0, "已投递", "blue", "fa-paper-plane"),
    REJECTED(1, "未通过", "red", "fa-times-circle"),
    HIRED(2, "已录用", "purple", "fa-check-circle");

    private final int code;
    private final String text;
    private final String color;
    private final String icon;

    ApplicationStatusEnum(int code, String text, String color, String icon) {
        this.code = code;
        this.text = text;
        this.color = color;
        this.icon = icon;
    }
} 