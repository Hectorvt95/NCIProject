/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hmvt.simplerestservice.config;

/**
 *
 * @author marti
 */
public class LightcastConfig {
    private final String clientId;
    private final String clientSecret;
    private String accessToken;
    private long tokenExpiryTime;

    public LightcastConfig(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    // Getters and setters
    public String getClientId() { return clientId; }
    public String getClientSecret() { return clientSecret; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public long getTokenExpiryTime() { return tokenExpiryTime; }
    public void setTokenExpiryTime(long tokenExpiryTime) { this.tokenExpiryTime = tokenExpiryTime; }

    public boolean isTokenExpired() {
        return System.currentTimeMillis() >= tokenExpiryTime;
    }
    
}
