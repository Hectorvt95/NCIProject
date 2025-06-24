/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hmvt.simplerestservice.service;

/**
 *
 * @author marti
 */

import com.hmvt.simplerestservice.model.ResumeInfo;
import com.hmvt.simplerestservice.parser.ResumeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ResumeService {
    
    @Autowired
    private ResumeParser resumeParser;
    
    /**
     * Parse a resume from file path
     * @param filePath Path to the resume file
     * @return ResumeInfo containing extracted information
     * @throws IOException if file cannot be read
     */
    
    
    public ResumeInfo parseResumeFile(String filePath) throws IOException {
        try {
            ResumeInfo resumeInfo = resumeParser.parseResume(filePath);
            
            // Log successful parsing
            System.out.println("Successfully parsed resume: " + filePath);
            System.out.println("Found " + resumeInfo.getSkills().size() + " skills");
            System.out.println("Found " + resumeInfo.getJobTitles().size() + " job titles");
            
            return resumeInfo; 
            
        } catch (IOException e) {
            System.err.println("Failed to parse resume file: " + filePath + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error parsing resume: " + e.getMessage());
            throw new RuntimeException("Failed to parse resume: " + e.getMessage(), e);
        }
    }
    

    
    /**
     * Validate if a resume file can be processed
     * @param filePath Path to the resume file
     * @return true if file exists and has valid extension
     */
    public boolean validateResumeFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        
        String lowerPath = filePath.toLowerCase();
        boolean validExtension = lowerPath.endsWith(".txt") || 
                                lowerPath.endsWith(".doc") || 
                                lowerPath.endsWith(".docx");
        
        if (!validExtension) {
            System.err.println("Invalid file extension. Supported: .txt, .doc, .docx");
            return false;
        }
        
        java.io.File file = new java.io.File(filePath);
        if (!file.exists()) {
            System.err.println("File does not exist: " + filePath);
            return false;
        }
        
        if (!file.canRead()) {
            System.err.println("Cannot read file: " + filePath);
            return false;
        }
        
        return true;
    }
    
    /**
     * Get summary statistics from a ResumeInfo object
     * @param resumeInfo The resume information to analyze
     * @return String summary of the resume
     */
    public String getResumeSummary(ResumeInfo resumeInfo) {
        if (resumeInfo == null) {
            return "No resume data available";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("=== RESUME SUMMARY ===\n");
        summary.append("Skills found: ").append(resumeInfo.getSkills().size()).append("\n");
        summary.append("Job titles found: ").append(resumeInfo.getJobTitles().size()).append("\n");
        summary.append("Locations found: ").append(resumeInfo.getLocations().size()).append("\n");
        summary.append("Job experiences found: ").append(resumeInfo.getJobExperiences().size()).append("\n");
        summary.append("Education: ").append(resumeInfo.getLatestEducation()).append("\n");
        
        if (!resumeInfo.getSkills().isEmpty()) {
            summary.append("Top skills: ");
            List<String> skills = resumeInfo.getSkills();
            int showSkills = Math.min(5, skills.size());
            for (int i = 0; i < showSkills; i++) {
                summary.append(skills.get(i));
                if (i < showSkills - 1) summary.append(", ");
            }
            if (skills.size() > 5) {
                summary.append(" and ").append(skills.size() - 5).append(" more...");
            }
            summary.append("\n");
        }
        
        return summary.toString();
    }
    
    /**
     * Check if Lightcast API is working by testing with sample text
     * @return true if API is responding correctly
     */
    public boolean testLightcastConnection() {
        try {
            // Test with a simple text containing known skills
            //String testContent = "Software Engineer with experience in Java, Python, and React";
            //ResumeInfo testResult = resumeParser.parseResume("test_resume.txt"); // This would be a test file
            String fullPath = "C:\\Users\\marti\\OneDrive\\Documents\\NetBeansProjects\\DistSyst\\Project_CvService\\src\\main\\resources\\test_resume.pdf";
            ResumeInfo testResult = resumeParser.parseResume(fullPath); // This would be a test file
            
            // If we get skills back, API is working
            return !testResult.getSkills().isEmpty();
            
        } catch (Exception e) {
            System.err.println("Lightcast API test failed: " + e.getMessage());
            return false;
        }
    }
}