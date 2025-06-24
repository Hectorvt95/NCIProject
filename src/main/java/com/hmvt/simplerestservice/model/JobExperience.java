/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hmvt.simplerestservice.model;

/**
 *
 * @author marti
 */
public class JobExperience {
    private final String title;
    private final String company;
    private final String duration;
    private final String location;

    public JobExperience(String title, String company, String duration, String location) {
        this.title = title;
        this.company = company;
        this.duration = duration;
        this.location = location;
    }

    // Getters
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getDuration() { return duration; }
    public String getLocation() { return location; }

    @Override
    public String toString() {
        return String.format("%s at %s (%s) - %s", title, company, duration, location);
    } 
}
