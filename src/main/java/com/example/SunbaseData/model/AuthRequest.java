package com.example.SunbaseData.model;

public class AuthRequest {
    public String login_id;
    public String password;

    public AuthRequest(String login_id, String password) {
        this.login_id = login_id;
        this.password = password;
    }

    public String getLoginId() {
        return login_id;
    }

    public void setLoginId(String loginId) {
        this.login_id = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
// Constructors, getters, setters
}