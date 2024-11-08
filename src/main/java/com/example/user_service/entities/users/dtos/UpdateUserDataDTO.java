package com.example.user_service.entities.users.dtos;

import jakarta.validation.constraints.Size;

public record UpdateUserDataDTO(

    @Size(max = 30, message = "Instagram username must be at maximum 30 characters")
    String instagramProfile,

    @Size(min = 25, max = 25, message = "Spotify id must be 25 characters long")
    String spotifyProfile

){
}
