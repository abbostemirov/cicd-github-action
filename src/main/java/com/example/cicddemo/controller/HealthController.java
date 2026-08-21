package com.example.cicddemo.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {



    @GetMapping("/check-health")
    public String health() {
        return "up";
    }
}
