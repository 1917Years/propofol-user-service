package propofol.userservice.api.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import propofol.userservice.api.auth.service.Oauth2Service;
import propofol.userservice.api.common.jwt.TokenDto;
import propofol.userservice.api.member.controller.dto.ResponseDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class Oauth2Controller {

    private final Oauth2Service oauth2Service;

    @GetMapping("/kakao/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto kakaoLogin(@RequestParam String code){
        TokenDto tokenDto = oauth2Service.getToken(code);
        return new ResponseDto<>(HttpStatus.OK.value(), "success", "카카오 로그인 성공!", tokenDto);
    }
}
