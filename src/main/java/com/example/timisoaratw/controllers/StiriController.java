package com.example.timisoaratw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StiriController {
    @GetMapping("/stiri")
    public String stiri() {
        return "stiri";
    }
}
