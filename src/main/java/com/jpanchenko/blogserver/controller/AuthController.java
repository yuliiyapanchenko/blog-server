package com.jpanchenko.blogserver.controller;

import com.jpanchenko.blogserver.auth.TokenHelper;
import com.jpanchenko.blogserver.exception.UnathorizedException;
import com.jpanchenko.blogserver.exception.UnprocessableEntityException;
import com.jpanchenko.blogserver.model.User;
import com.jpanchenko.blogserver.repository.UserRepository;
import com.jpanchenko.blogserver.validation.EmailValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@RestController
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private TokenHelper tokenHelper;

    @PostMapping(value = "/signup", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public User signUp(@RequestBody User user) throws UnprocessableEntityException {
        if (!EmailValidator.validate(user.getEmail())) {
            throw new UnprocessableEntityException("You must provide valid email and password");
        }

        if (Objects.nonNull(userRepository.findByEmail(user.getEmail()))) {
            throw new UnprocessableEntityException("Email is in use");
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singletonList("regularUser"));
        User dbUser = userRepository.save(user);

        return User.builder()
                .token(tokenHelper.generateToken(dbUser))
                .roles(dbUser.getRoles())
                .build();
    }

    @PostMapping(value = "/signin", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public User signIn(@RequestBody User user) throws UnprocessableEntityException, UnathorizedException {
        if (!EmailValidator.validate(user.getEmail())) {
            throw new UnprocessableEntityException("You must provide valid email and password");
        }

        User dbUser = userRepository.findByEmail(user.getEmail());
        if (Objects.isNull(dbUser) || !bCryptPasswordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            throw new UnathorizedException();
        }

        return User.builder()
                .token(tokenHelper.generateToken(dbUser))
                .roles(dbUser.getRoles())
                .build();
    }
}
