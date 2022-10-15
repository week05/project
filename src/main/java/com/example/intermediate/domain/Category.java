package com.example.intermediate.domain;

import java.util.Arrays;

public enum Category {
    UNKNOWN(0, "UNKNOWN"),
    VIDEO(1, "video"),
    GAME(2, "game"),
    DRAMA(3, "drama");

    private String name;
    private int id;

    Category(int num, String name) {
        this.name = name;
        this.id = num;

    }
        public static Category findById(Long id) {
        return Arrays.stream(values())
                .filter(category -> category.id == id)
                .findAny()
                .orElse(UNKNOWN);
    }
}
