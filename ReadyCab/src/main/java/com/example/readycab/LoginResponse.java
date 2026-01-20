package com.example.readycab;

public class LoginResponse {
    private String token;
    private UserData data;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UserData getData() { return data; }
    public void setData(UserData data) { this.data = data; }
}
