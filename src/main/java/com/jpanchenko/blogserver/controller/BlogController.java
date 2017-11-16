package com.jpanchenko.blogserver.controller;

import com.jpanchenko.blogserver.exception.NotFoundException;
import com.jpanchenko.blogserver.model.Post;
import com.jpanchenko.blogserver.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@RestController
public class BlogController {

    @Autowired
    PostRepository postRepository;

    @GetMapping(value = "/posts", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @PostMapping(value = "/posts", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Post addPost(@RequestBody Post post) {
        return postRepository.save(post);
    }

    @GetMapping(value = "/posts/{postId}", produces = APPLICATION_JSON_UTF8_VALUE)
    public Post getPost(@PathVariable("postId") String postId) throws NotFoundException {
        Post dbPost = postRepository.findById(postId);

        if (Objects.isNull(dbPost)) {
            throw new NotFoundException();
        }

        return dbPost;
    }

    @PutMapping(value = "/posts/{postId}", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public Post updatePost(@PathVariable("postId") String postId, @RequestBody Post post) throws NotFoundException {
        Post dbPost = postRepository.findById(postId);

        if (Objects.isNull(dbPost)) {
            throw new NotFoundException();
        }

        return postRepository.save(Post.builder()
                .id(postId)
                .title(post.getTitle())
                .body(post.getBody())
                .date(post.getDate())
                .build());
    }

    @DeleteMapping(value = "/posts/{postId}")
    public void deletePost(@PathVariable("postId") String postId) throws NotFoundException {
        Post dbPost = postRepository.findById(postId);

        if (Objects.isNull(dbPost)) {
            throw new NotFoundException();
        }

        postRepository.deleteById(postId);
    }
}
