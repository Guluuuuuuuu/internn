package com.gulu.internship07.controller;

import com.gulu.internship07.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User(1L, "Alice", "alice@example.com"));
        users.add(new User(2L, "Bob", "bob@example.com"));
        return users;
    }
} 