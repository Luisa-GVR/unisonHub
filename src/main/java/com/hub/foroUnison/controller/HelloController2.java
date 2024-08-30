package com.hub.foroUnison.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/hello2")
public class HelloController2 {

    @GetMapping
    public String helloWorld() {
        return "hello2";
    }

}
