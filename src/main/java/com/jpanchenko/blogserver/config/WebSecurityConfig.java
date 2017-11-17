package com.jpanchenko.blogserver.config;

import com.jpanchenko.blogserver.auth.TokenHelper;
import com.jpanchenko.blogserver.auth.filter.JWTAuthenticationFilter;
import com.jpanchenko.blogserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String ADMIN = "admin";

    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private TokenHelper tokenHelper;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        OrRequestMatcher orRequestMatcher = new OrRequestMatcher(
                new AntPathRequestMatcher("/posts/**", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/posts/**", HttpMethod.POST.name()),
                new AntPathRequestMatcher("/posts/{postId}/**", HttpMethod.PUT.name()),
                new AntPathRequestMatcher("/posts/{postId}/**", HttpMethod.DELETE.name())
        );

        http.csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.GET, "/posts").authenticated()
                .antMatchers(HttpMethod.POST, "/posts/**").hasAuthority(ADMIN)
                .antMatchers(HttpMethod.PUT, "/posts/{postId}/**").hasAuthority(ADMIN)
                .antMatchers(HttpMethod.DELETE, "/posts/{postId}/**").hasAuthority(ADMIN)
                .antMatchers(HttpMethod.POST, "/signup", "/signin").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(new Http401AuthenticationEntryPoint("Unauthorized"))
                .and()
                .addFilterBefore(new JWTAuthenticationFilter(orRequestMatcher, userRepository, tokenHelper),
                        UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
}
