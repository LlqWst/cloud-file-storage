package dev.lqwd.cloudfilestorage.controller;


import dev.lqwd.cloudfilestorage.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {

    @GetMapping
    public String showHomePage(@AuthenticationPrincipal User user) {


        return "home for user: " + user.getUsername();
    }
}
