package com.jpanchenko.blogserver.auth.filter;

import com.jpanchenko.blogserver.auth.TokenHelper;
import com.jpanchenko.blogserver.exception.UnathorizedException;
import com.jpanchenko.blogserver.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final UserRepository userRepository;
    private final TokenHelper tokenHelper;

    public JWTAuthenticationFilter(RequestMatcher requiresAuthentication, UserRepository userRepository, TokenHelper tokenHelper) {
        super(requiresAuthentication);
        this.userRepository = userRepository;
        this.tokenHelper = tokenHelper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        Optional<Authentication> authentication = tokenHelper.getAuthentication(httpServletRequest, userRepository);
        if (authentication.isPresent()) {
            return authentication.get();
        } else {
            throw new UnathorizedException();
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        chain.doFilter(request, response);
    }
}
