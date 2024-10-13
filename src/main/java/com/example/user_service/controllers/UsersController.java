package com.example.user_service.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.entities.users.dtos.UpdateUserDTO;
import com.example.user_service.services.TokenService;
import com.example.user_service.services.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping(value = "/user", produces = {"application/json"})
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    // ======================================================
    // ================  Public Endpoints  ==================
    // ======================================================



    // ======================================================
    // ================  USER Role Endpoints  ===============
    // ======================================================

    @SuppressWarnings("rawtypes")
    @GetMapping(value = "/api-test")
    public ResponseEntity addUser(){

        return userService.createUser("6706e4559534da5fd4dbe68d");
    }

    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/update-user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addPost(@Valid @RequestBody UpdateUserDTO data, BindingResult result, 
                                    @RequestHeader("Authorization") String token){

        if (result.hasErrors()){
            String error = result.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(error);
        }
        
        String userId = tokenService.getIdFromToken(token);

        return userService.updateUser(userId, data.instagramProfile(), data.spotifyProfile());
    }

    // ======================================================
    // ===============  ADMIN Role Endpoints  ===============
    // ======================================================

    @SuppressWarnings("rawtypes")
    @DeleteMapping("/{userId}/remove")
    public ResponseEntity deleteAnyPost(@PathVariable String userId){

        return ResponseEntity.ok().body("ID: " + userId);
    }
}

