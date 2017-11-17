package com.jpanchenko.blogserver.auth;

import com.jpanchenko.blogserver.model.User;
import com.jpanchenko.blogserver.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Component
public class TokenHelper {
    @Value("${jwt.secret}")
    public String SECRET;

    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
    private final long EXPIRATION_TIME = 1000 * 60;

    private static final String BEARER_PREFIX = "Bearer ";

    public Optional<Authentication> getAuthentication(HttpServletRequest request, UserRepository userRepository) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(token)) {
            String email = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(StringUtils.substringAfter(token, BEARER_PREFIX))
                    .getBody()
                    .getSubject();

            User dbUser = userRepository.findByEmail(email);

            if (Objects.nonNull(dbUser)) {
                return Optional.of(new UsernamePasswordAuthenticationToken(dbUser.getEmail(), null, dbUser.getAuthorities()));
            }
        }

        return Optional.empty();
    }

    public String generateToken(User dbUser) {
        String jwt = Jwts.builder()
                .setSubject(dbUser.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SIGNATURE_ALGORITHM, SECRET)
                .compact();

        return BEARER_PREFIX + jwt;
    }
}
