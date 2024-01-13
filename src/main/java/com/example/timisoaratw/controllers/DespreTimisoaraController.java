package com.example.timisoaratw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DespreTimisoaraController {
    @GetMapping("/despre")
    public String despre() {
        return "despreTimisoara";
    }
}
