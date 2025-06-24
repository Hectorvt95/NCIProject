/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hmvt.simplerestservice.controller;

/**
 *
 * @author marti
 */
import com.hmvt.simplerestservice.model.ResumeInfo;
import com.hmvt.simplerestservice.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "*")
public class ResumeController {
    
    @Autowired
    private ResumeService resumeService;
    
    @PostMapping("/parse")
    public ResponseEntity<ResumeInfo> parseResumeFile(@RequestParam String filePath) {
        try {
            if (!resumeService.validateResumeFile(filePath)) {
                return ResponseEntity.badRequest().build();
            }
            
            ResumeInfo resumeInfo = resumeService.parseResumeFile(filePath);
            return ResponseEntity.ok(resumeInfo);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Resume parser service is running");
    }
}

