package com.example.user_service.services.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.user_service.entities.users.User;
import com.example.user_service.entities.users.dtos.BasicProfileDTO;
import com.example.user_service.entities.users.dtos.BasicProfilesResponseDTO;

@Service
public class UserProfileService {

    @Autowired
    private UserService userService;
    
    
    @SuppressWarnings("rawtypes")
    public ResponseEntity getBasicProfiles(List<String> userIds){

        BasicProfilesResponseDTO response = new BasicProfilesResponseDTO();

        for (String userId : userIds) {

            
            ResponseEntity<User> userResponse = userService.findUserById(userId);
            if (!userResponse.getStatusCode().is2xxSuccessful()) {
                continue;
            }
            
            User user = userResponse.getBody();

            System.out.println(user.getName());

            BasicProfileDTO profile = new BasicProfileDTO(user.getId(), user.getName(),  user.getThumbnail());

            response.addProfile(profile);

        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
