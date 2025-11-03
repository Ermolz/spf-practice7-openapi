package com.example.openapi.controller;

import com.example.openapi.dto.FeedbackDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("page", "home");
        model.addAttribute("feedback", new FeedbackDto());
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("page", "about");
        return "about";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("page", "admin");
        return "admin";
    }

    @PostMapping("/feedback")
    public String handleFeedback(@ModelAttribute("feedback") FeedbackDto dto, Model model) {
        model.addAttribute("page", "home");
        model.addAttribute("sent", true);
        model.addAttribute("feedback", new FeedbackDto());
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("page", "login");
        return "login";
    }

}