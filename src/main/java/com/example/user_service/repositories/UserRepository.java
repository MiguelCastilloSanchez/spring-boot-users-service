package com.example.user_service.repositories;

import com.example.user_service.entities.users.User;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    @Query(value = "{}", fields = "{_id: 1}")
    List<String> findAllUserIds();
}
