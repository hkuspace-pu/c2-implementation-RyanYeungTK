package com.example.cw2_apps.domain.model;


public class AuthSession {
    public final String username;
    public final Role role;
    public AuthSession(String username, Role role) {
        this.username = username; this.role = role;
    }
}

