package com.example.timisoaratw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class AdminMesajeController {

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "SYSTEM";
    private static final String DB_PASSWORD = "db";

    @GetMapping("/adminmesaje")
    public String adminmesaje(Model model) {
        List<Map<String, Object>> mesaje = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT nume, email, mesaj FROM timisoaramesaje";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Map<String, Object> mesaj = Map.of(
                                "nume", resultSet.getString("nume"),
                                "email", resultSet.getString("email"),
                                "mesaj", resultSet.getString("mesaj")
                        );
                        mesaje.add(mesaj);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

            return "redirect:/eroare";
        }

        model.addAttribute("mesaje", mesaje);
        return "adminmesaje";
    }
}
