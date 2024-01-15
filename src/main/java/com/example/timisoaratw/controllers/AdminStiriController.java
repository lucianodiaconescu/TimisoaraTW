package com.example.timisoaratw.controllers;

import com.example.timisoaratw.models.Stire;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminStiriController {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "SYSTEM";
    private static final String DB_PASSWORD = "db";
    private Stire stireToEdit;

    @GetMapping("/adminstiri")
    public String sti(Model model) {
        List<Stire> stiri = fetchStiriFromDatabase();
        model.addAttribute("stiri", stiri);
        model.addAttribute("stireToEdit", stireToEdit);
        return "adminstiri";
    }

    @PostMapping("/adminstiri")
    public String adaugaStire(
            @RequestParam String nume,
            @RequestParam String data,
            @RequestParam String descriere,
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "oldNume", required = false) String oldNume,
            Model model) {

        if ("edit".equals(action)) {
            stireToEdit = getStireByName(oldNume);
        } else if ("update".equals(action)) {
            if (stireToEdit != null) {
                updateStireInDatabase(stireToEdit.getNume(), nume, data, descriere);
                stireToEdit = null; // Reset the stireToEdit after updating
            }
        } else {
            // If stireToEdit is null, it means we are adding a new news article
            saveStireInDatabase(nume, data, descriere);
        }

        List<Stire> stiri = fetchStiriFromDatabase();
        model.addAttribute("stiri", stiri);

        return "redirect:/adminstiri";
    }

    @PostMapping("/adminstiri/delete")
    public String stergeStire(@RequestParam String nume, Model model) {
        deleteStireFromDatabase(nume);

        List<Stire> stiri = fetchStiriFromDatabase();
        model.addAttribute("stiri", stiri);

        return "redirect:/adminstiri";
    }

    @GetMapping("/adminstiri/edit")
    public String editStire(@RequestParam String nume, Model model) {
        stireToEdit = getStireByName(nume);
        return "redirect:/adminstiri";
    }

    private List<Stire> fetchStiriFromDatabase() {
        List<Stire> stiri = new ArrayList<>();

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

        return stiri;
    }

    private void saveStireInDatabase(String nume, String data, String descriere) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO timisoarastiri (nume, data, descriere) VALUES (?, ?, ?)";
            try (PreparedStatement statement = con.prepareStatement(sql)) {
                statement.setString(1, nume);
                statement.setString(2, data);
                statement.setString(3, descriere);

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateStireInDatabase(String oldNume, String newNume, String data, String descriere) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE timisoarastiri SET nume = ?, data = ?, descriere = ? WHERE nume = ?";
            try (PreparedStatement statement = con.prepareStatement(sql)) {
                statement.setString(1, newNume);
                statement.setString(2, data);
                statement.setString(3, descriere);
                statement.setString(4, oldNume);

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteStireFromDatabase(String nume) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DELETE FROM timisoarastiri WHERE nume = ?";
            try (PreparedStatement statement = con.prepareStatement(sql)) {
                statement.setString(1, nume);

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Stire getStireByName(String nume) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT nume, data, descriere FROM timisoarastiri WHERE nume = ?";
            try (PreparedStatement statement = con.prepareStatement(sql)) {
                statement.setString(1, nume);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String data = resultSet.getString("data");
                        String descriere = resultSet.getString("descriere");
                        return new Stire(nume, data, descriere);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
