package com.example.silverbridgeX_user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class RootController {

    @GetMapping("/health")
    public String healthCheck() {
        return "User server is healthy";
    }
}
