package com.jpanchenko.blogserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.ServletException;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnathorizedException extends ServletException {
}
