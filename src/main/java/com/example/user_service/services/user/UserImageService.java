package com.example.user_service.services.user;

import java.io.IOException;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.user_service.entities.users.User;
import com.example.user_service.repositories.UserRepository;
import com.example.user_service.services.AzureBlobService;

@Service
public class UserImageService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AzureBlobService azureBlobService;

    @Value("${storage.url}")
    private String containerURL;

    @SuppressWarnings("rawtypes")
    public ResponseEntity updateImage(String userId, MultipartFile image) throws IOException{

        if(image == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty image");
        if(!isFileSizeValid(image)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Size exceeds 16 MB");
        if(!isFileExtensionValid(image)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image format not valid");

        ResponseEntity<User> response = userService.findUserById(userId);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        
        User user = response.getBody();

        String originalFileName = image.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

        String pfpURL = containerURL + userId + fileExtension;
        String pfpThumbnailURL = containerURL + userId + "_thumbnail" + fileExtension;

        if (user.getPfpURL() != null) azureBlobService.deleteBlob(user.getPfpURL(), containerURL);
        if (user.getPfpThumbnailURL() != null) azureBlobService.deleteBlob(user.getPfpThumbnailURL(), containerURL);

        azureBlobService.upload(image, pfpURL, containerURL);

        MultipartFile thumbnail = createThumbnail(image, pfpThumbnailURL);
        azureBlobService.upload(thumbnail, pfpThumbnailURL, containerURL);

        user.setPfpURL(pfpURL);
        user.setPfpThumbnailURL(pfpThumbnailURL);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).build();
        
    }

    private boolean isFileSizeValid(MultipartFile image) {

        int validSize = 16 * 1024 * 1024;
        return image.getSize() <= validSize;

    }

    private boolean isFileExtensionValid(MultipartFile image) {

        String contentType = image.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"));

    }

    private MultipartFile createThumbnail(MultipartFile image, String thumbnailFileName) throws IOException {
        BufferedImage originalImage = ImageIO.read(image.getInputStream());
        Image scaledImage = originalImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        BufferedImage thumbnailImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    
        Graphics2D g2d = thumbnailImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
    
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbnailImage, "png", baos);
        byte[] thumbnailBytes = baos.toByteArray();
    
        return new MockMultipartFile(
            thumbnailFileName,
            thumbnailFileName,
            image.getContentType(),
            thumbnailBytes            
        );
    }
    

    
}
