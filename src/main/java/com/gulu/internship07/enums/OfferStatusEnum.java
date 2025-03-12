package com.gulu.internship07.enums;

import lombok.Getter;

@Getter
public enum OfferStatusEnum {
    PENDING(0, "待接受", "yellow", "fa-hourglass"),
    ACCEPTED(1, "已接受", "green", "fa-thumbs-up"),
    REJECTED(2, "已拒绝", "red", "fa-thumbs-down"),
    CANCELLED(3, "已取消", "gray", "fa-ban");

    private final int code;
    private final String text;
    private final String color;
    private final String icon;

    OfferStatusEnum(int code, String text, String color, String icon) {
        this.code = code;
        this.text = text;
        this.color = color;
        this.icon = icon;
    }
} 