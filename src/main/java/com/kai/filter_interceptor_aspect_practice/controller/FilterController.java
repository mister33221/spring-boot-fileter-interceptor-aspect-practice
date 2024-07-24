package com.kai.filter_interceptor_aspect_practice.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FilterController {

    @GetMapping("/hello")
    @Operation(summary = "Hello")
    public String hello() {
        System.out.println("hello in FilterController");
        return "hello";
    }

}
