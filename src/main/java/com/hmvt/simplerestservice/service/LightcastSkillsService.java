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

@Service
public class LightcastSkillsService {
    // Using the Skills API endpoints instead of Open Skills
    private static final String AUTH_URL = "https://auth.emsicloud.com/connect/token";
    private static final String SKILLS_API_URL = "https://emsiservices.com/skills/versions/latest";
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    @Autowired
    private LightcastProperties lightcastProperties;
    
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
        
        System.out.println("=== AUTHENTICATION ===");
        System.out.println("Client ID: " + lightcastProperties.getClientId());
        System.out.println("Scope: " + lightcastProperties.getScope());
        
        String credentials = lightcastProperties.getClientId() + ":" + lightcastProperties.getClientSecret();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        
        String requestBody = "grant_type=client_credentials&scope=" + lightcastProperties.getScope();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AUTH_URL))
                .header("Authorization", "Basic " + encodedCredentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("Auth Response Status: " + response.statusCode());
        
        if (response.statusCode() == 200) {
            JsonNode tokenResponse = objectMapper.readTree(response.body());
            accessToken = tokenResponse.get("access_token").asText();
            int expiresIn = tokenResponse.get("expires_in").asInt();
            
            tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000L) - 60000; // 1 minute buffer
            
            System.out.println("✅ Successfully authenticated with Lightcast API");
        } else {
            throw new RuntimeException("Failed to authenticate: HTTP " + response.statusCode() + " - " + response.body());
        }
    }
    
    
    // Extract skills using Skills API search instead of extraction endpoint
    public Set<String> extractSkillsFromText(String text) throws IOException, InterruptedException {
        System.out.println("=== SKILL EXTRACTION VIA SEARCH ===");
        authenticate();
        
        // Get all skills from the API and then match against text
        Set<String> allSkills = getAllSkills();
        Set<String> foundSkills = matchSkillsInText(text, allSkills);
        
        System.out.println("✅ Found " + foundSkills.size() + " skills in text");
        System.out.println(foundSkills);
        return foundSkills;
    }
    
    
    // Get all skills from Lightcast Skills API
       private Set<String> getAllSkills() throws IOException, InterruptedException {
        System.out.println("Fetching all skills from API (this may take a moment)...");
        
        Set<String> allSkills = new HashSet<>();
        
        // First, try without pagination to see the response structure
        System.out.println("Testing API response structure...");
        String skillsUrl = SKILLS_API_URL + "/skills";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(skillsUrl))
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("Skills API Response Status: " + response.statusCode());
        
        if (response.statusCode() == 200) {
            JsonNode skillsResponse = objectMapper.readTree(response.body());
            
            // Print response structure for debugging
            System.out.println("Response structure keys: ");
            skillsResponse.fieldNames().forEachRemaining(field -> System.out.println("  - " + field));
            
            // get all skills from this single response
            JsonNode skillsArray = skillsResponse.get("data");
            if (skillsArray == null) {
                skillsArray = skillsResponse.get("skills");
            }

            if (skillsArray != null && skillsArray.isArray()) {
                System.out.println("Found " + skillsArray.size() + " skills in single response");
                for (JsonNode skill : skillsArray) {
                    String skillName = skill.get("name").asText().trim();
                    if (skillName != null) {
                        allSkills.add(skillName);
                    }
                }
            } else {
                System.out.println("No skills array found. Available fields:");
                skillsResponse.fieldNames().forEachRemaining(System.out::println);
            }
            
        } else {
            System.err.println("Failed to fetch skills: HTTP " + response.statusCode());
            System.err.println("Response body: " + response.body());
        }
        
        System.out.println("🎉 Successfully loaded " + allSkills.size() + " total skills from Lightcast API!");
        
        return allSkills;
    }
    
  
     
    // Match skills in text against the skills database extracted from the API
    private Set<String> matchSkillsInText(String text, Set<String> allSkills) {
        Set<String> foundSkills = new HashSet<>();
        String lowerText = text.toLowerCase();
        
        // Match skills against the text
        for (String skill : allSkills) {
            if (lowerText.contains(skill.toLowerCase())) {
                foundSkills.add(skill);
            }
        }
        
        System.out.println("Matched skills:");
        for (String skill : foundSkills) {
            System.out.println("  - " + skill);
        }
        
        return foundSkills;
    }
    
  
}