package com.trading.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    // Redirect to /login when user visits the root URL
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }
}
