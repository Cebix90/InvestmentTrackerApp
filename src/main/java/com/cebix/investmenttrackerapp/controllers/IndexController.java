package com.cebix.investmenttrackerapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String getMainPage() {
        return "index";
    }
}
