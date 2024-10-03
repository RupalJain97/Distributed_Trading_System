package com.trading.controller;

import com.trading.model.UserModel;
import com.trading.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // return login.html
    }

    @PostMapping("/login")
    public String login(@RequestParam String userid, @RequestParam String password, Model model) {
        UserModel user = userService.validateUser(userid, password);
        if (user != null) {
            model.addAttribute("user", user);
            return "redirect:/user"; // redirect to user dashboard page
        } else {
            model.addAttribute("error", "Invalid userid or password");
            return "login"; // stay on login page with error message
        }
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String userid, @RequestParam String password,
            HttpSession session, Model model) {
        System.out.println("Registering the user..... ");
        System.out.println("Received username: " + username);
        System.out.println("Received userid: " + userid);
        System.out.println("Received password: " + password);
        UserModel user = userService.registerUser(username, userid, password);
        session.setAttribute("user", user);
        return "redirect:/user"; // redirect to dashboard after successful registration
    }

    @GetMapping("/user")
    public String showUserDashboard(Model model, HttpSession session) {

        UserModel user = (UserModel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // Redirect to login if session expires
        }
        
        System.out.println("Redirecting to /user...");
        System.out.println("Received username: " + user.getUsername());
        System.out.println("Received userid: " + user.getUserid());
        System.out.println(user);
        model.addAttribute("user", user);
        System.out.println(model.getAttribute("user"));
        return "userDashboard"; // Return the dashboard HTML page
    }
}