package com.jpanchenko.blogserver.repository;

import com.jpanchenko.blogserver.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
}
