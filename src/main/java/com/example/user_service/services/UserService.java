package com.example.user_service.services;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.user_service.entities.users.User;
import com.example.user_service.repositories.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;


    @SuppressWarnings("rawtypes")
    public ResponseEntity createUser(String userId){
        try{
            User user = new User();
            user.setId(userId);
            user.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now().minus(Duration.ofHours(6))));
            user.setProfilePhoto("default_pfp_.png");
            userRepository.save(user);
        return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @SuppressWarnings("rawtypes")
    public ResponseEntity updateUser(String userId, String instagramProfile, String spotifyProfile){

        ResponseEntity<User> response = findUserById(userId);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        
        User user = response.getBody();

        if(instagramProfile != null) user.setInstagramProfile(instagramProfile);
        if(spotifyProfile != null) user.setSpotifyProfile(spotifyProfile);
        
        userRepository.save(user);

        return ResponseEntity.ok().body("User data updated");
    }

    private ResponseEntity<User> findUserById(String userId) {
        try{
            Optional<User> optionalUser = userRepository.findById(userId);
        
            if (optionalUser.isPresent()) {
                return ResponseEntity.ok(optionalUser.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
