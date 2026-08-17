package com.example.cicddemo.config;

import com.example.cicddemo.entity.Item;
import com.example.cicddemo.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ItemRepository itemRepository;

    public DataSeeder(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) {
        if (itemRepository.count() == 0) {
            itemRepository.save(new Item("Docker"));
            itemRepository.save(new Item("Kubernetes"));
            itemRepository.save(new Item("GitHub Actions"));
        }
    }
}
