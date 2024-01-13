package com.example.timisoaratw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EvenimenteController {
    @GetMapping("/evenimente")
    public String evenimente() {
        return "evenimente";
    }
}
