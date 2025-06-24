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
    
    private List<String> jobTitles;
    private List<String> skills;
    private String latestEducation;
    private List<String> locations;
    private List<JobExperience> jobExperiences;

    public ResumeInfo() {
        this.jobTitles = new ArrayList<>();
        this.skills = new ArrayList<>();
        this.locations = new ArrayList<>();
        this.jobExperiences = new ArrayList<>();
    }

    // Getters and setters
    public List<String> getJobTitles() { return jobTitles; }
    public void setJobTitles(List<String> jobTitles) {
        this.jobTitles = jobTitles; 
    }

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

    public List<JobExperience> getJobExperiences() { return jobExperiences; }
    public void setJobExperiences(List<JobExperience> jobExperiences) {
        this.jobExperiences = jobExperiences; 
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESUME ANALYSIS ===\n");
        sb.append("Job Titles: ").append(jobTitles).append("\n");
        sb.append("Skills: ").append(skills).append("\n");
        sb.append("Latest Education: ").append(latestEducation).append("\n");
        sb.append("Locations: ").append(locations).append("\n");
        sb.append("Job Experiences:\n");
        
        for (JobExperience exp : jobExperiences) {
            sb.append("  - ").append(exp).append("\n");
        }
        return sb.toString();
    }
    
}
   
