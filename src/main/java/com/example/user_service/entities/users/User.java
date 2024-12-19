package com.example.user_service.entities.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;

    private String timestamp;
    private String instagramProfile;
    private String spotifyProfile;

    private String pfpURL;
    private String pfpThumbnailURL;
}
