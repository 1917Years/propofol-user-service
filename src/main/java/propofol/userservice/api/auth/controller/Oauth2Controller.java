package propofol.userservice.api.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import propofol.userservice.api.auth.service.Oauth2Service;
import propofol.userservice.api.common.jwt.TokenDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class Oauth2Controller {

    private final Oauth2Service oauth2Service;

    @GetMapping("/kakao/login")
    public TokenDto kakaoLogin(@RequestParam String code){
        TokenDto token = oauth2Service.getToken(code);
        System.out.println("token = " + token);
        return token;
    }
}
