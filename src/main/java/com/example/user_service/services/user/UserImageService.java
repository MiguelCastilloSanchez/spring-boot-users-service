package com.example.user_service.services.user;

import java.io.IOException;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Image;


import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.user_service.entities.users.User;
import com.example.user_service.repositories.UserRepository;

@Service
public class UserImageService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @SuppressWarnings("rawtypes")
    public ResponseEntity updateImage(String userId, MultipartFile image) throws IOException{

        if(image == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty image");
        if(!isFileSizeValid(image)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Size exceeds 4 MB");
        if(!isFileExtensionValid(image)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image format not valid");

        ResponseEntity<User> response = userService.findUserById(userId);
        if (!response.getStatusCode().is2xxSuccessful()) {

            return response;

        }
        
        User user = response.getBody();

        byte[] profilePicture = image.getBytes();
        byte[] thumbnail = createThumbnail(image);
        
        user.setProfilePicture(profilePicture);
        user.setThumbnail(thumbnail);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).build();
        
    }

    @SuppressWarnings("rawtypes")
    public ResponseEntity getImage(String userId){

        ResponseEntity<User> response = userService.findUserById(userId);
        if (!response.getStatusCode().is2xxSuccessful()) {

            return response;

        }

        User user = response.getBody();
        byte[] profilePicture = user.getProfilePicture();

        if (profilePicture == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png")).body(profilePicture);

    }

    private boolean isFileSizeValid(MultipartFile image) {

        int validSize = 4 * 1024 * 1024;
        return image.getSize() <= validSize;

    }

    private boolean isFileExtensionValid(MultipartFile image) {

        String contentType = image.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"));

    }

    private byte[] createThumbnail(MultipartFile image) throws IOException {

        BufferedImage originalImage = ImageIO.read(image.getInputStream());
        Image scaledImage = originalImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        BufferedImage thumbnailImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2d = thumbnailImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbnailImage, "jpg", baos);
        return baos.toByteArray();
        
    }
}
