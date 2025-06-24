/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hmvt.simplerestservice.service;
import com.hmvt.simplerestservice.config.LightcastProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author marti
 */

@Service
public class LightcastSkillsService {
    private static final String AUTH_URL = "https://auth.emsicloud.com/connect/token";
    //private static final String AUTH_URL = "https://auth.lightcast.io/connect/token";
    private static final String SKILLS_API_URL = "https://skills.lightcast.io/versions/latest";
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    @Autowired
    private LightcastProperties lightcastProperties; //this is called a constructor injection so the lightcast object has all the properties already set.
    
    // Token management
    private String accessToken;
    private long tokenExpiryTime;
    
    public LightcastSkillsService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    // Check if token is expired
    private boolean isTokenExpired() {
        return System.currentTimeMillis() >= tokenExpiryTime;
    }
    
    // Authenticate and get access token
    private void authenticate() throws IOException, InterruptedException {
        if (accessToken != null && !isTokenExpired()) {
            return; // Token is still valid
        }
        
        String credentials = lightcastProperties.getClientId() + ":" + lightcastProperties.getClientSecret();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        
        String requestBody = "grant_type=client_credentials&scope=" + lightcastProperties.getScope(); //the scope is what we want, in this case, in the application propierties we hace emsi_open which is for access to the skills data
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AUTH_URL))
                .header("Authorization", "Basic " + encodedCredentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)) //here is what i am asking for, from requestBody and its the skills
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            JsonNode tokenResponse = objectMapper.readTree(response.body());
            accessToken = tokenResponse.get("access_token").asText();
            int expiresIn = tokenResponse.get("expires_in").asInt();
            
            tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000L) - 60000; // 1 minute buffer
            
            System.out.println("Successfully authenticated with Lightcast API");
        } else {
            throw new RuntimeException("Failed to authenticate: " + response.body());
        }
    }
    
    // Extract skills from text (the cv made text) using Lightcast extraction endpoint
    public Set<String> extractSkillsFromText(String text) throws IOException, InterruptedException {
        authenticate();
        
        String extractUrl = SKILLS_API_URL + "/skills/extract";
        
        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody. put("text", text);
        requestBody.put("confidence_threshold", 0.2); // Adjust as needed
        
        String jsonBody = objectMapper.writeValueAsString(requestBody);  //this step will write the skill values from Lightcast, will extract them
        
        //this is the request to extract the values and then write them in the jsonBody string
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(extractUrl))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        Set<String> extractedSkills = new HashSet<>();
        
        if (response.statusCode() == 200) {
            JsonNode extractResponse = objectMapper.readTree(response.body());
            JsonNode skillsArray = extractResponse.get("data");
            
            if (skillsArray != null && skillsArray.isArray()) {
                for (JsonNode skill : skillsArray) {
                    JsonNode skillInfo = skill.get("skill");
                    if (skillInfo != null) {
                        extractedSkills.add(skillInfo.get("name").asText());
                    }
                }
            }
        } else {
            System.err.println("Failed to extract skills: " + response.body());
       
            return null;
        }
        
        return extractedSkills;
    }
    
}    

