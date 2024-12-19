package com.example.user_service.services.user;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.user_service.entities.users.User;
import com.example.user_service.entities.users.dtos.BasicProfileDTO;
import com.example.user_service.entities.users.dtos.BasicProfilesResponseDTO;
import com.example.user_service.repositories.UserRepository;

@Service
public class UserProfileService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    
    
    @SuppressWarnings("rawtypes")
    public ResponseEntity getBasicProfiles(List<String> userIds){

        BasicProfilesResponseDTO response = new BasicProfilesResponseDTO();

        for (String userId : userIds) {

            
            ResponseEntity<User> userResponse = userService.findUserById(userId);
            if (!userResponse.getStatusCode().is2xxSuccessful()) {
                continue;
            }
            
            User user = userResponse.getBody();

            BasicProfileDTO profile = new BasicProfileDTO(user.getId(), user.getName(),  user.getPfpThumbnailURL());

            response.addProfile(profile);

        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @SuppressWarnings("rawtypes")
    public ResponseEntity getCompleteProfile(String userId){
    
        ResponseEntity<User> userResponse = userService.findUserById(userId);
        if (!userResponse.getStatusCode().is2xxSuccessful()) {
            return userResponse;
        }
        
        User user = userResponse.getBody();

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @SuppressWarnings("rawtypes")
    public ResponseEntity getAllUsers(){

        return getBasicProfiles(getIdsFromRepository());
        
    }

    private List<String> getIdsFromRepository(){

        List<String> jsonStrings = userRepository.findAllUserIds();

        List<String> ids = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        for (String jsonString : jsonStrings) {
            JsonNode rootNode;
            try {
                rootNode = mapper.readTree(jsonString);
                String id = rootNode.get("_id").get("$oid").asText();
                ids.add(id);
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return ids;
    }
}
