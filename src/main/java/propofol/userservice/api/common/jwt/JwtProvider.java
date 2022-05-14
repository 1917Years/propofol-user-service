package propofol.userservice.api.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import propofol.userservice.api.common.exception.ExpiredRefreshTokenException;
import propofol.userservice.api.common.properties.JwtProperties;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private Key key;

    @PostConstruct
    private void createKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto createJwt(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority()).collect(Collectors.joining(", "));

        Date expirationDate = new Date(System.currentTimeMillis()
                + Long.parseLong(jwtProperties.getExpirationTime()));

        String token = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("role", authorities)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.createTokenDto()
                .type(jwtProperties.getType())
                .accessToken(token)
                .refreshToken(createRefreshToken())
                .build();
    }

    public TokenDto createReJwt(String memberId, String memberRole){
        Date expirationDate = new Date(System.currentTimeMillis()
                + Long.parseLong(jwtProperties.getExpirationTime()));

        String token = Jwts.builder()
                .setSubject(memberId)
                .claim("role", memberRole)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.createTokenDto()
                .type(jwtProperties.getType())
                .accessToken(token)
                .refreshToken(createRefreshToken())
                .build();
    }

    public String createRefreshToken(){
        Date refreshExpirationTime = new Date(System.currentTimeMillis() +
                Long.parseLong(jwtProperties.getRefreshExpirationTime()));

        return Jwts.builder()
                .setExpiration(refreshExpirationTime)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUserId(String token){
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return claims.getSubject();
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

    public boolean isRefreshTokenValid(String refreshToken){
        try {
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
            return !jwtParser.parseClaimsJws(refreshToken).getBody().getExpiration().before(new Date());
        }catch (Exception e){
            if(e instanceof ExpiredJwtException){
                throw new ExpiredRefreshTokenException("Please Re-Login.");
            }
        }
        return false;
    }

    public boolean isTokenValid(String bearerToken){
        try {
            String token = bearerToken.replace("Bearer ", "").toString();
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
            return !jwtParser.parseClaimsJws(token).getBody().getExpiration().before(new Date());
        }catch (Exception e){
            if(e instanceof ExpiredJwtException){
                return false;
            }
        }
        return false;
    }
}
