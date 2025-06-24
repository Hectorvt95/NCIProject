/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hmvt.simplerestservice;
//import com.hmvt.simplerestservice.model.ResumeInfo;
import com.hmvt.simplerestservice.parser.ResumeParser;
import com.hmvt.simplerestservice.service.ResumeService;
import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * @author marti
 */
public class test {
    
    public static void main(String[] args) {
            // Start Spring Boot context to get proper dependency injection
            ConfigurableApplicationContext context = SpringApplication.run(SimpleRestServiceApplication.class, args);
                  
           
           try {
               // Get the ResumeParser bean from Spring context (with all dependencies injected)
               ResumeService resumeService = context.getBean(ResumeService.class);
               //String fullPath = "C:\\Users\\marti\\OneDrive\\Documents\\NetBeansProjects\\DistSyst\\Project_CvService\\src\\main\\resources\\cv.pdf";
               System.out.println("=== TESTING RESUME PARSER ===");
   
               System.out.println(resumeService.testLightcastConnection());
               
           }  catch(Exception e) {
               System.out.println("Spring context error: " + e.getMessage());
           } finally {
               // Close Spring context
               context.close();
           }
       }
    
}
