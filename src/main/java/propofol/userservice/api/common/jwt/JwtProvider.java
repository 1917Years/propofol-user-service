package propofol.userservice.api.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtProvider {

    private final Key key;
    private final String expirationTime;
    private final String type;

    public JwtProvider(@Value("${token.secret}") String secret,
                       @Value("${token.expiration_time}") String expirationTime,
                       @Value("${token.type}") String type) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationTime = expirationTime;
        this.type = type;
    }

    public TokenDto createJwt(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority()).collect(Collectors.joining(", "));

        Date expirationDate = new Date(System.currentTimeMillis()
                + Long.parseLong(expirationTime));

        String token = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("role", authorities)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.createTokenDto()
                .type(type)
                .accessToken(token)
                .refreshToken(null)
                .expirationDate(expirationDate.getTime())
                .build();
    }

    public Authentication getUserInfo(String token){
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        String memberId = claims.getSubject();
        String authority = claims.get("role").toString();

        Collection<? extends GrantedAuthority> at = Arrays.stream(authority.split(","))
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());

        UserDetails principal = new User(memberId, "", at);


        return new UsernamePasswordAuthenticationToken(principal, "", at);
    }
}
