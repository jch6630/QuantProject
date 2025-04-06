package com.jch.DTO;

public class TokenJson {
    private String token;
    private String timestamp;

    // 기본 생성자 추가
    public TokenJson() {
        this.token = "";
        this.timestamp = "";
    }

    // 기존 생성자 (인자 받는)
    public TokenJson(String token, String timestamp) {
        this.token = token;
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
