package com.admin.dto;

import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    private String name;
    
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    public UpdateProfileRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}