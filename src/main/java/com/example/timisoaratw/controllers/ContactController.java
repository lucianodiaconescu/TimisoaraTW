package com.example.timisoaratw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Controller
public class ContactController {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "SYSTEM";
    private static final String DB_PASSWORD = "db";
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
    @PostMapping("/contact")
    public String trimiteMesaj(
            @RequestParam("nume") String nume,
            @RequestParam("email") String email,
            @RequestParam("mesaj") String mesaj) {

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO timisoaramesaje (nume, email, mesaj) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nume);
                statement.setString(2, email);
                statement.setString(3, mesaj);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/eroare";
        }
        return "redirect:/contact";
    }
}
