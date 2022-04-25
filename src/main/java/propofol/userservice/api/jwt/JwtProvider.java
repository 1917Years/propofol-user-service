package propofol.userservice.api.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private final Environment env;
    private Key key;

    public TokenDto createJwt(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority()).collect(Collectors.joining(", "));

        String secret = env.getProperty("token.secret");
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        key = Keys.hmacShaKeyFor(keyBytes);

        Date expirationDate = new Date(System.currentTimeMillis()
                + Long.parseLong(env.getProperty("token.expiration_time")));

        String token = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("role", authorities)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        String type = env.getProperty("token.type");
        return TokenDto.createTokenDto()
                .type(type)
                .accessToken(token)
                .refreshToken(null)
                .expirationDate(expirationDate.getTime())
                .build();
    }
}
