/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hmvt.simplerestservice;
//import com.hmvt.simplerestservice.model.ResumeInfo;
import com.hmvt.simplerestservice.parser.ResumeParser;
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
            
/*
            ResumeParser newParse = new ResumeParser();


            String fullPath = "C:\\Users\\marti\\OneDrive\\Documents\\NetBeansProjects\\DistSyst\\SimpleRestService\\src\\main\\resources\\cv.pdf";
            try{
                System.out.println(newParse.parseResume(fullPath));
            }catch(IOException e){
                System.out.println("Something went wrong\n" + e);
            }
*/           
           
           try {
               // Get the ResumeParser bean from Spring context (with all dependencies injected)
               ResumeParser resumeParser = context.getBean(ResumeParser.class);

               String fullPath = "C:\\Users\\marti\\OneDrive\\Documents\\NetBeansProjects\\DistSyst\\SimpleRestService\\src\\main\\resources\\cv.pdf";

               System.out.println("=== TESTING RESUME PARSER ===");
               
               //ResumeInfo result = resumeParser.parseResume(fullPath);
               
               //System.out.println(result);

               System.out.println(resumeParser.readFile(fullPath));
               
           } catch(IOException e) {
               System.out.println("Something went wrong\n" + e);
           } catch(Exception e) {
               System.out.println("Spring context error: " + e.getMessage());
           } finally {
               // Close Spring context
               context.close();
           }
       }
    
}
