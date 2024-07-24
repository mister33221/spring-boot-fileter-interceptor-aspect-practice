package com.kai.filter_interceptor_aspect_practice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interceptor")
public class InterceptorTestController {

    @GetMapping("/hello")
    public String hello() {
        System.out.println("hello in InterceptorController");
        return "hello";
    }

}
