package com.example.user_service.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.user_service.entities.users.dtos.UpdateUserDataDTO;
import com.example.user_service.services.TokenService;
import com.example.user_service.services.user.UserImageService;
import com.example.user_service.services.user.UserProfileService;
import com.example.user_service.services.user.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping(value = "/user", produces = {"application/json"})
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserImageService userImageService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private TokenService tokenService;

    // ======================================================
    // ================  Public Endpoints  ==================
    // ======================================================

    /**
     * Returns all basic information from various users
     *
     * @param userIds List of user ids to be fetch
     * @return ResponseEntity containing all users basic information (id, name and thumbnail)
     */
    @SuppressWarnings("rawtypes")
    @PostMapping(value="/get-basic-profiles", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getBasicProfiles(@RequestBody List<String> userIds){

        return userProfileService.getBasicProfiles(userIds);
    }

    /**
     * Returns all information from an user
     *
     * @param userId user id to be fetch
     * @return ResponseEntity containing all information from an user
     */
    @SuppressWarnings("rawtypes")
    @GetMapping(value="/{userId}")
    public ResponseEntity getBasicProfiles(@PathVariable String userId){

        return userProfileService.getCompleteProfile(userId);
    }

    // ======================================================
    // ================  USER Role Endpoints  ===============
    // ======================================================

    /**
     * Update user's information
     *
     * @param data Object containing all the data to update
     * @param result Object checking the validation of the user's new data
     * @param token String containing the token from the user interacting
     * @return ResponseEntity indicating success or failure of user's data update
     */
    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/update-user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateUser(@Valid @RequestBody UpdateUserDataDTO data, BindingResult result, 
                                    @RequestHeader("Authorization") String token){

        if (result.hasErrors()){
            String error = result.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(error);
        }
        
        String userId = tokenService.getIdFromToken(token);

        return userService.updateUserData(userId, data);
    }

    /**
     * Update user's profile picture
     *
     * @param image Object containing the image file
     * @param token String containing the token from the user interacting
     * @return ResponseEntity indicating success or failure of user's image update
     */
    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/update-profile-picture")
    public ResponseEntity updateProfilePicture(@RequestParam("image") MultipartFile image,
                                                @RequestHeader("Authorization") String token){

        String userId = tokenService.getIdFromToken(token);

        try {
            return userImageService.updateImage(userId, image);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    /**
     * Returns user's profile picture
     *
     * @param token String containing the token from the user interacting
     * @return ResponseEntity containing the users profile picture
     */
    @SuppressWarnings("rawtypes")
    @GetMapping(value = "/get-profile-picture")
    public ResponseEntity getProfilePicture(@RequestHeader("Authorization") String token){

        String userId = tokenService.getIdFromToken(token);

        return userImageService.getImage(userId);

    }


    /**
     * Deletes an user (Differs from remove in removing just itself)
     *
     * @param token String containing the token from the user interacting
     * @return ResponseEntity containing the succes or failure of the request
     */
    @SuppressWarnings("rawtypes")
    @DeleteMapping(value = "/delete")
    public ResponseEntity deleteUser(@RequestHeader("Authorization") String token){

        String userId = tokenService.getIdFromToken(token);

        return userService.deleteUser(userId);
    }

    // ======================================================
    // ===============  ADMIN Role Endpoints  ===============
    // ======================================================

    /**
     * Removes an user (Differs from delete in removing any user, not itself)
     *
     * @param userId String containing the user id to be removed
     * @return ResponseEntity indicating success or failure of user's removal
     */
    @SuppressWarnings("rawtypes")
    @DeleteMapping("/{userId}/remove")
    public ResponseEntity removeUser(@PathVariable String userId){

        return userService.deleteUser(userId);

    }

    /**
     * Returns basic information from all users
     *
     * @return ResponseEntity containing all users basic information (id, name and thumbnail)
     */
    @SuppressWarnings("rawtypes")
    @PostMapping(value="/get-all-profiles")
    public ResponseEntity getAllUsers(){

        return userProfileService.getAllUsers();

    }
}

