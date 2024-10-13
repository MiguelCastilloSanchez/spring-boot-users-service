package com.example.user_service.entities.blacklistedtokens;

import lombok.AllArgsConstructor; 
import lombok.Data; 
import lombok.NoArgsConstructor; 
import org.springframework.data.annotation.Id; 
import org.springframework.data.redis.core.RedisHash;
  
@Data 
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "blacklisted_tokens")  
public class BlacklistedToken { 
  
    @Id
    private String token;

}
