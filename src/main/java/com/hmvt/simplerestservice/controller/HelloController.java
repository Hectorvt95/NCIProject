/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hmvt.simplerestservice.controller;

import java.time.LocalTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    private String message = "Hello world";
    
    @GetMapping("/hello")
    public String sayHello() {
        return message;
    }
    
    @PutMapping("/updatemessage")
    public String updateMessage(@RequestBody String newMessage) {
        newMessage = "Go raibh maith agat";
        System.out.println("Received Message " + newMessage);
        return "Updated Message: "+ LocalTime.now() + " " +  newMessage ;
    }

}
 

