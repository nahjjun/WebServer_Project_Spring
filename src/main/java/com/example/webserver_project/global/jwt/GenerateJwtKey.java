package com.example.webserver_project.global.jwt;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;

// jwt키를 직접 생성해주는 함수
public class GenerateJwtKey {
    public static void main(String[] args) {
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        String encoded = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println(encoded);
    }
}
