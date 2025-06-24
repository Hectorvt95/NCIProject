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
        System.out.println("Fetching skills from Skills API...");
        
        String skillsUrl = SKILLS_API_URL + "/skills";
        Set<String> allSkills = new HashSet<>();
        
        // Get skills in batches (the API might paginate)
        String nextUrl = skillsUrl + "?limit=1000"; // Start with first page
        
        while (nextUrl != null && allSkills.size() < 5000) { // Limit to prevent excessive API calls
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(nextUrl))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonNode skillsResponse = objectMapper.readTree(response.body());
                JsonNode skillsArray = skillsResponse.get("data");
                
                if (skillsArray != null && skillsArray.isArray()) {
                    for (JsonNode skill : skillsArray) {
                        String skillName = skill.get("name").asText();
                        allSkills.add(skillName.toLowerCase()); // Store in lowercase for matching
                    }
                }
                
                // Check for next page
                JsonNode links = skillsResponse.get("links");
                nextUrl = (links != null && links.has("next")) ? links.get("next").asText() : null;
                
                System.out.println("Loaded " + allSkills.size() + " skills so far...");
  
            } else {
                System.err.println("Failed to fetch skills: HTTP " + response.statusCode());
                break;
            }
        }
        
        System.out.println("Total skills loaded: " + allSkills.size());
        return allSkills;
    }
    
   
    
    // Match skills in text against the skills database extracted from the API
    private Set<String> matchSkillsInText(String text, Set<String> allSkills) {
        Set<String> foundSkills = new HashSet<>();
        String lowerText = text.toLowerCase();
        
        // Direct matching
        for (String skill : allSkills) {
            if (lowerText.contains(skill)) {
                // Capitalize first letter for display
                String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                foundSkills.add(displaySkill);
            }
        }
        
        return foundSkills;
    }
    
  
}