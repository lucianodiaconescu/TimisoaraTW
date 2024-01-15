package com.example.timisoaratw.controllers;

import com.example.timisoaratw.models.Eveniment;
import com.example.timisoaratw.models.Stire;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminLoggedController {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "SYSTEM";
    private static final String DB_PASSWORD = "db";

    @GetMapping("/adminlogged")
    public String adminlogged(Model model) {
        List<Stire> stiri = new ArrayList<>();
        List<Eveniment> evenimente = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT nume, data, descriere FROM timisoarastiri";
            try (PreparedStatement statement = con.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String nume = resultSet.getString("nume");
                    String data = resultSet.getString("data");
                    String descriere = resultSet.getString("descriere");

                    Stire stire = new Stire(nume, data, descriere);
                    stiri.add(stire);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT nume, data, descriere FROM timisoaraevenimente";
            try (PreparedStatement statement = con.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String nume = resultSet.getString("nume");
                    String data = resultSet.getString("data");
                    String descriere = resultSet.getString("descriere");

                    Eveniment eveniment = new Eveniment(nume, data, descriere);
                    evenimente.add(eveniment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("stiri", stiri);
        model.addAttribute("evenimente", evenimente);
        return "adminlogged";
    }
}