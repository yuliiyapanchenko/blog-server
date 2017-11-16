package com.jpanchenko.blogserver.repository;

import com.jpanchenko.blogserver.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    Post findById(String id);

    Post findByTitle(String title);

    List<Post> findByDate(Date date);

    Long deleteById(String id);
}
