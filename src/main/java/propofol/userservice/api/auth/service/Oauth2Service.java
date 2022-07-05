package propofol.userservice.api.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import propofol.userservice.api.auth.service.dto.KakaoTokenResponseDto;
import propofol.userservice.api.auth.service.dto.KakaoUserInfoDto;
import propofol.userservice.api.common.jwt.JwtProvider;
import propofol.userservice.api.common.jwt.TokenDto;
import propofol.userservice.api.common.properties.KakaoOauth2Properties;
import propofol.userservice.domain.member.entity.Authority;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;

@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2Service {

    private final MemberService memberService;
    private final RestTemplate restTemplate;
    private final KakaoOauth2Properties kakao;
    private final BCryptPasswordEncoder encoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public TokenDto getToken(String code){

        ResponseEntity<KakaoTokenResponseDto> tokenResponse = getAccessToken(code);
        if(tokenResponse.getStatusCode() != HttpStatus.OK) throw new RuntimeException("카카오 로그인 오류 발생");

        KakaoTokenResponseDto responseBody = tokenResponse.getBody();
        String accessToken = responseBody.getAccessToken();

        ResponseEntity<KakaoUserInfoDto> userInfoResponse = getUserInfo(accessToken);
        KakaoUserInfoDto userInfo = userInfoResponse.getBody();

        Authentication authenticate = createAuthenticate(userInfo);

        return jwtProvider.createJwt(authenticate);
    }

    private Authentication createAuthenticate(KakaoUserInfoDto userInfo) {
        String email = "kakao" + userInfo.getId() + "@kakao.com";
        String password = encoder.encode(kakao.getClientSecret());

        if(!memberService.isExistByEmail(email)){
            Member member = Member.createMember()
                    .email(email)
                    .password(password)
                    .phoneNumber(null)
                    .degree(null)
                    .score(null)
                    .nickname(null)
                    .username(null)
                    .birth(null)
                    .authority(Authority.ROLE_USER)
                    .totalRecommend(0L)
                    .build();
            memberService.saveMember(member);
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, kakao.getClientSecret());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        return authenticate;
    }

    private ResponseEntity<KakaoUserInfoDto> getUserInfo(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        ResponseEntity<KakaoUserInfoDto> userInfoResponse = restTemplate.exchange(kakao.getUserInfoUri(),
                HttpMethod.GET, new HttpEntity<>(null, httpHeaders), KakaoUserInfoDto.class);
        return userInfoResponse;
    }

    private ResponseEntity<KakaoTokenResponseDto> getAccessToken(String code) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", kakao.getGrantType());
        map.add("client_id", kakao.getClientId());
        map.add("redirect_uri", kakao.getRedirectUri());
        map.add("code", code);
        map.add("client_secret", kakao.getClientSecret());
        return restTemplate.postForEntity(kakao.getGetTokenUri(), createHttpEntity(map, MediaType.APPLICATION_FORM_URLENCODED), KakaoTokenResponseDto.class);
    }

    private HttpEntity<Object> createHttpEntity(MultiValueMap map, MediaType mediaType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(mediaType);
        return new HttpEntity<>(map, httpHeaders);
    }

}
