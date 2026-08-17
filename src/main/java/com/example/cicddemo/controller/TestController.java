package com.example.cicddemo.controller;

import com.example.cicddemo.repository.ItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Demo controller: Postgres bazasidan ma'lumot o'qib qaytaradi.
 * Maqsad — konteyner ichidagi app bazaga muvaffaqiyatli ulanayotganini tekshirish.
 */
@RestController
@RequestMapping("/api")
public class TestController {

    private final ItemRepository itemRepository;

    public TestController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/items")
    public List<ItemResponse> getItems() {
        return itemRepository.findAll()
                .stream()
                .map(ItemResponse::from)
                .toList();
    }

    @GetMapping("/test")
    public String test() {
        return "TEST";
    }
}
