package com.example.cicddemo.controller;

import com.example.cicddemo.entity.Item;

import java.time.Instant;

public record ItemResponse(Long id, String name, Instant createdAt) {

    public static ItemResponse from(Item item) {
        return new ItemResponse(item.getId(), item.getName(), item.getCreatedAt());
    }
}
