package com.example.timisoaratw.controllers;

import com.example.timisoaratw.models.Eveniment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class EvenimenteController {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "SYSTEM";
    private static final String DB_PASSWORD = "db";
    @GetMapping("/evenimente")
    public String evenimente(@RequestParam(name = "nume", defaultValue = "") String nume, Model model) {
        List<Eveniment> evenimente = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            String sql = "SELECT nume, data, descriere FROM timisoaraevenimente WHERE LOWER(nume) LIKE LOWER(?)";
            try (PreparedStatement statement = con.prepareStatement(sql)) {
                statement.setString(1, "%" + nume + "%");

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String evenimentNume = resultSet.getString("nume");
                        String data = resultSet.getString("data");
                        String descriere = resultSet.getString("descriere");

                        Eveniment eveniment = new Eveniment(evenimentNume, data, descriere);
                        evenimente.add(eveniment);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("evenimente", evenimente);
        return "evenimente";
    }

}
