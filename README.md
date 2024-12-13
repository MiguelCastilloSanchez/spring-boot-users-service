# Users Service API

This repository contains a Spring Boot application that manages user profiles and user-related functionalities for a specific application. This service integrates user authentication from [Authentication Service](https://github.com/MiguelCastilloSanchez/spring-boot-auth-service). This repository also contains the CI/CD Pipeline for building and deploying the service to Dockerhub and deployment repository at https://github.com/MiguelCastilloSanchez/helm-chart-app-services

---

## Endpoints

### **1. Fetch Basic Profiles**
**Endpoint**: `POST /user/get-basic-profiles`  
**Description**: Retrieves basic profile information (ID, name, and thumbnail) for a list of users.

**Request Body**:  
```json
[
  "userId1",
  "userId2"
]
```

---

### **2. Fetch Complete Profile**
**Endpoint**: `GET /user/{userId}`  
**Description**: Retrieves complete profile information for a specific user.

**Path Parameter**:  
- `userId` (String): ID of the user whose profile is being requested.

---

### **3. Update User Information**
**Endpoint**: `POST /user/update-user`  
**Description**: Updates the information of the authenticated user.

**Headers**:  
- `Authorization: Bearer <token>`

**Request Body**:  
```json
{
  "instagramProfile": "string",
  "spotifyProfile": "string",
}
```

---

### **4. Update Profile Picture**
**Endpoint**: `POST /user/update-profile-picture`  
**Description**: Updates the profile picture of the authenticated user.

**Headers**:  
- `Authorization: Bearer <token>`

**Request Parameter**:  
- `image` (MultipartFile): The profile picture file to upload.

---

### **5. Fetch Profile Picture**
**Endpoint**: `GET /user/get-profile-picture`  
**Description**: Retrieves the profile picture of the authenticated user.

**Headers**:  
- `Authorization: Bearer <token>`

---

### **6. Delete User**
**Endpoint**: `DELETE /user/delete`  
**Description**: Deletes the authenticated user’s account.

**Headers**:  
- `Authorization: Bearer <token>`

---

## Admin Role Endpoints

### **7. Remove Any User**
**Endpoint**: `DELETE /user/{userId}/remove`  
**Description**: Allows an admin to remove any user from the system.

**Path Parameter**:  
- `userId` (String): ID of the user to be removed.

---

### **8. Fetch All Users’ Basic Profiles**
**Endpoint**: `POST /user/get-all-profiles`  
**Description**: Retrieves basic profile information (ID, name, and thumbnail) for all users.

---

## Notes
- **Authorization**: Some endpoints require an `Authorization` header with a valid JWT token.
- **Validation**: Inputs are validated, and appropriate error messages are returned for invalid requests.
- **Roles**: Specific endpoints are restricted to users with the `ADMIN` role.

