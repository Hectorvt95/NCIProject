/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hmvt.simplerestservice.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marti
 */
public class ResumeInfo {
    
    private List<String> skills;
    private String latestEducation;
    private List<String> locations;

    public ResumeInfo() {
        this.skills = new ArrayList<>();
        this.locations = new ArrayList<>();
    }

    // Getters and setters
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { 
        this.skills = skills; 
    }

    public String getLatestEducation() { return latestEducation; }
    public void setLatestEducation(String latestEducation) {
        this.latestEducation = latestEducation; 
    }

    public List<String> getLocations() { return locations; }
    public void setLocations(List<String> locations) {
        this.locations = locations; 
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESUME ANALYSIS ===\n");
        sb.append("Skills: ").append(skills).append("\n");
        sb.append("Latest Education: ").append(latestEducation).append("\n");
        sb.append("Locations: ").append(locations).append("\n");
        return sb.toString();
    }
    
}
   
