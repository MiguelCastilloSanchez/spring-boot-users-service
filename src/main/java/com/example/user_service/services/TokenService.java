package com.example.user_service.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import com.example.user_service.repositories.BlacklistedTokenRepository;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;
    
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secret);
            
            boolean isBlacklisted = blacklistedTokenRepository.findByToken(token).isPresent();
            if (isBlacklisted) {
                throw new JWTVerificationException("Token has been revoked.");
            }

            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    public String getRoleFromToken(String token) {
        return decodeJWT(token).getClaim("role").asString();
    }

    public String getIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return decodeJWT(token).getClaim("id").asString();
    }

    private DecodedJWT decodeJWT(String token) {
        Algorithm algorithm = Algorithm.HMAC256(this.secret);
        var verifier = JWT.require(algorithm).withIssuer("auth-api").build();
        var decodedJWT = verifier.verify(token);
        return decodedJWT;
    }
}
