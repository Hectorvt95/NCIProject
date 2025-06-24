/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hmvt.simplerestservice.parser;

/**
 *
 * @author marti
 */

import com.hmvt.simplerestservice.model.JobExperience;
import com.hmvt.simplerestservice.model.ResumeInfo;
import com.hmvt.simplerestservice.service.LightcastSkillsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ResumeParser {
    
    @Autowired
    private LightcastSkillsService lightcastSkillsService;
    
    // Common job titles
    private static final String[] JOB_TITLES = {
        "Software Engineer", "Software Developer", "Senior Developer", "Lead Developer",
        "Full Stack Developer", "Frontend Developer", "Backend Developer", "Web Developer",
        "Data Scientist", "Data Analyst", "Machine Learning Engineer", "DevOps Engineer",
        "Project Manager", "Product Manager", "Technical Lead", "Engineering Manager",
        "QA Engineer", "Test Engineer", "System Administrator", "Database Administrator",
        "UX Designer", "UI Designer", "Business Analyst", "Solutions Architect",
        "Cloud Engineer", "Security Engineer", "Mobile Developer", "Game Developer"};
    
    // Parse resume from file path
    public ResumeInfo parseResume(String filePath) throws IOException {
        //Path path = Paths.get(filePath);
        String content = readFile(filePath) ;
        return extractInformation(content);
    }
    
    public String readFile(String filePath) throws IOException {
        try {
            AutoDetectParser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler(-1); // No limit on content length
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            
            try (InputStream stream = Files.newInputStream(Paths.get(filePath))) {
                parser.parse(stream, handler, metadata, context);
                return handler.toString();
            }
        } catch (Exception e) {
            throw new IOException("Failed to read PDF file: " + e.getMessage(), e);
        }
    }
    
    private ResumeInfo extractInformation(String content) {
        ResumeInfo info = new ResumeInfo();
        System.out.println("Extracted content length: " + content.length());

        // Extract job titles
        info.setJobTitles(extractJobTitles(content));
        
        // Extract skills using Lightcast API
        info.setSkills(extractSkills(content));
        
        // Extract education
        info.setLatestEducation(extractLatestEducation(content));
        
        // Extract locations
        info.setLocations(extractLocations(content));
        
        // Extract job experiences
        info.setJobExperiences(extractJobExperiences(content));
        
        return info;
    }
    
    private List<String> extractJobTitles(String content) {
        //this set<> works like a hash map but doesnt allow duplicated values
        Set<String> foundTitles = new HashSet<>();
        String lowerContent = content.toLowerCase();
        
        for (String title : JOB_TITLES) {
            if (lowerContent.contains(title.toLowerCase())) {
                foundTitles.add(title);
            }
        }
        
        return new ArrayList<>(foundTitles);
    }
    
    private List<String> extractSkills(String content) {
        Set<String> foundSkills = new HashSet<>();
        
        try {
            // Use Lightcast API for skill extraction
            foundSkills = lightcastSkillsService.extractSkillsFromText(content);
            System.out.println("Extracted " + foundSkills.size() + " skills using Lightcast API");
            
        } catch (Exception e) {
            System.err.println("Failed to use Lightcast API: " + e.getMessage());
            // Return empty set if API fails instead of local fallback
            foundSkills = new HashSet<>();
        }
        
        return new ArrayList<>(foundSkills);
    }
    
    private String extractLatestEducation(String content) {
        // Look for degree patterns
        Pattern degreePattern = Pattern.compile(
            "(bachelor|master|phd|doctorate|diploma|certificate)\\s+(?:of\\s+)?([^\\n\\r,]+)", 
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = degreePattern.matcher(content);
        String latestEducation = "";
        
        while (matcher.find()) {
            latestEducation = matcher.group(0).trim();
        }
        
        return latestEducation.isEmpty() ? "Not specified" : latestEducation;
    }
    
    private List<String> extractLocations(String content) {
        Set<String> locations = new HashSet<>();
        
        
        // Common location patterns
        Pattern locationPattern = Pattern.compile(
            "(?:location|address|city|state)\\s*:?\\s*([^\\n\\r]+)", 
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = locationPattern.matcher(content);
        while (matcher.find()) {
            String location = matcher.group(1).trim();
            if (location.length() > 2 && location.length() < 10) {
                locations.add(location);
            }
        }
        
        return locations.isEmpty() ? null : new ArrayList<>(locations);//if there is no location specified, put it null
    }
    
    private List<JobExperience> extractJobExperiences(String content) {
        List<JobExperience> experiences = new ArrayList<>();
        
        // Pattern to match job experience sections
        Pattern expPattern = Pattern.compile(
            "([A-Za-z\\s,]+?)\\s+(?:at|@)\\s+([A-Za-z\\s&.,]+?)\\s*(?:\\n|\\r|$|\\d{4}|\\d{1,2}/\\d{4})", 
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = expPattern.matcher(content);
        while (matcher.find()) {
            String title = matcher.group(1).trim();
            String company = matcher.group(2).trim();
            
            if (title.length() > 5 && title.length() < 50 && 
                company.length() > 2 && company.length() < 50) {
                
                experiences.add(new JobExperience(title, company, "Not specified", "Not specified"));
            }
        }
        
        return experiences;
    }
    
   
}