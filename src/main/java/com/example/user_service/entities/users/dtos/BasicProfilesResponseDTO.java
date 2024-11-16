package com.example.user_service.entities.users.dtos;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BasicProfilesResponseDTO{

    private List<BasicProfileDTO> profiles = new ArrayList<>();

    public void addProfile(BasicProfileDTO profile){
        this.profiles.add(profile);
    }
}