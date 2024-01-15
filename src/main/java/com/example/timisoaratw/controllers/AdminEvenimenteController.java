package com.example.timisoaratw.controllers;

import com.example.timisoaratw.models.Eveniment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminEvenimenteController {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "SYSTEM";
    private static final String DB_PASSWORD = "db";
    private Eveniment evenimentToEdit;

    @GetMapping("/adminevenimente")
    public String ev(Model model) {
        List<Eveniment> evenimente = fetchEvenimenteFromDatabase();
        model.addAttribute("evenimente", evenimente);
        model.addAttribute("evenimentToEdit", evenimentToEdit);
        return "adminevenimente";
    }

    @PostMapping("/adminevenimente")
    public String adaugaEveniment(
            @RequestParam String nume,
            @RequestParam String data,
            @RequestParam String descriere,
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "oldNume", required = false) String oldNume,
            Model model) {

        if ("edit".equals(action)) {
            evenimentToEdit = getEvenimentByName(oldNume);
        } else if ("update".equals(action)) {
            if (evenimentToEdit != null) {
                updateEvenimentInDatabase(evenimentToEdit.getNume(), nume, data, descriere);
                evenimentToEdit = null; // Reset the evenimentToEdit after updating
            }
        } else {
            // If evenimentToEdit is null, it means we are adding a new event
            saveEvenimentInDatabase(nume, data, descriere);
        }

        List<Eveniment> evenimente = fetchEvenimenteFromDatabase();
        model.addAttribute("evenimente", evenimente);

        return "redirect:/adminevenimente";
    }

    @PostMapping("/adminevenimente/delete")
    public String stergeEveniment(@RequestParam String nume, Model model) {
        deleteEvenimentFromDatabase(nume);

        List<Eveniment> evenimente = fetchEvenimenteFromDatabase();
        model.addAttribute("evenimente", evenimente);

        return "redirect:/adminevenimente";
    }

    @GetMapping("/adminevenimente/edit")
    public String editEveniment(@RequestParam String nume, Model model) {
        evenimentToEdit = getEvenimentByName(nume);
        return "redirect:/adminevenimente";
    }

    private List<Eveniment> fetchEvenimenteFromDatabase() {
        List<Eveniment> evenimente = new ArrayList<>();

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

        return evenimente;
    }

    private void saveEvenimentInDatabase(String nume, String data, String descriere) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO timisoaraevenimente (nume, data, descriere) VALUES (?, ?, ?)";
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

    private void updateEvenimentInDatabase(String oldNume, String newNume, String data, String descriere) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE timisoaraevenimente SET nume = ?, data = ?, descriere = ? WHERE nume = ?";
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

    private void deleteEvenimentFromDatabase(String nume) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DELETE FROM timisoaraevenimente WHERE nume = ?";
            try (PreparedStatement statement = con.prepareStatement(sql)) {
                statement.setString(1, nume);

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Eveniment getEvenimentByName(String nume) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT nume, data, descriere FROM timisoaraevenimente WHERE nume = ?";
            try (PreparedStatement statement = con.prepareStatement(sql)) {
                statement.setString(1, nume);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String data = resultSet.getString("data");
                        String descriere = resultSet.getString("descriere");
                        return new Eveniment(nume, data, descriere);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
