package de.ait.g_67_shop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @Value("${messages.greeting}")
    private String greeting;

    @GetMapping
    public String getGreeting() {
        return greeting;
    }
}
