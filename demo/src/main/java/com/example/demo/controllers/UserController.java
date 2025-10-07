package com.example.demo.controllers;

import com.example.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class UserController
{
    private final UserRepository userRepository;
    @RequestMapping("/user/info")
    @ResponseBody
    public void getUserInfo(@RequestParam(name = "id") int userId)
    {

    }
}
