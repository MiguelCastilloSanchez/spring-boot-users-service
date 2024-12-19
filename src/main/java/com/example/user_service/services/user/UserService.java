package com.example.user_service.services.user;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.user_service.entities.users.User;
import com.example.user_service.entities.users.dtos.UpdateUserDataDTO;
import com.example.user_service.repositories.UserRepository;
import com.example.user_service.services.AzureBlobService;
import com.example.user_service.services.rabbitmq.RabbitSenderService;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitSenderService rabbitSenderService;

    @Value("${storage.url}")
    private String containerURL;

    @Autowired
    private AzureBlobService azureBlobService;


    @SuppressWarnings("rawtypes")
    public ResponseEntity createUser(String userId, String name){
        try{

            User user = new User();
            user.setId(userId);
            user.setName(name);
            user.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now().minus(Duration.ofHours(6))));
            userRepository.save(user);

            System.out.println("Created User With ID: " + userId);
            return ResponseEntity.status(HttpStatus.OK).build();

        } catch (Exception e){

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            
        }
    }

    @SuppressWarnings("rawtypes")
    public ResponseEntity updateUserData(String userId, UpdateUserDataDTO data){

        ResponseEntity<User> response = findUserById(userId);
        if (!response.getStatusCode().is2xxSuccessful()) {

            return response;

        }
        
        User user = response.getBody();

        if(data.instagramProfile() != null) user.setInstagramProfile(data.instagramProfile());
        if(data.spotifyProfile() != null) user.setSpotifyProfile(data.spotifyProfile());
        
        userRepository.save(user);

        return ResponseEntity.ok().body("User data updated");

    }

    @SuppressWarnings("rawtypes")
    public ResponseEntity deleteUser(String userId){

        ResponseEntity<User> response = findUserById(userId);
        if (!response.getStatusCode().is2xxSuccessful()) {

            return response;

        }

        User user = response.getBody();
        
        rabbitSenderService.sendMessage(userId);

        if (user.getPfpURL() != null) azureBlobService.deleteBlob(user.getPfpURL(), containerURL);
        if (user.getPfpThumbnailURL() != null) azureBlobService.deleteBlob(user.getPfpThumbnailURL(), containerURL);

        userRepository.deleteById(userId);

        return ResponseEntity.status(HttpStatus.OK).build();

    }

    protected ResponseEntity<User> findUserById(String userId) {
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
