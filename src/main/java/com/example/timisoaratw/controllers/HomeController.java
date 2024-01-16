    package com.example.timisoaratw.controllers;
    
    import com.example.timisoaratw.models.Eveniment;
    import com.example.timisoaratw.models.Stire;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.ResponseBody;
    
    
    import java.net.URI;
    import java.net.http.HttpClient;
    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;
    
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    
    import java.net.http.HttpClient;
    import java.net.http.HttpRequest;
    import java.net.http.HttpResponse;
    import java.util.Map;

    @Controller
    public class HomeController {
        private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
        private static final String DB_USER = "SYSTEM";
        private static final String DB_PASSWORD = "db";
        private static final String OPENAI_API_KEY = "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";


        @GetMapping("/")
        public String home(Model model) {
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
            return "home";
        }
        @PostMapping("/chat")
        @ResponseBody
        public String chat(@RequestBody String prompt) {
            try {
                String openaiEndpoint = "https://api.openai.com/v1/chat/completions";
                HttpClient client = HttpClient.newHttpClient();

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> requestBodyMap = Map.of(
                        "model", "gpt-3.5-turbo",
                        "messages", List.of(
                                Map.of("role", "system", "content", prompt)
                        )
                );
                String requestBody = objectMapper.writeValueAsString(requestBodyMap);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(openaiEndpoint))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + OPENAI_API_KEY)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return response.body();
                } else {
                    System.out.println("Error: " + response.statusCode());
                    System.out.println("Response Body: " + response.body());
                    return "Error: " + response.statusCode();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }
    }