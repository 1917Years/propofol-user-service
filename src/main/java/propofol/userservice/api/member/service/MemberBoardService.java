package propofol.userservice.api.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import propofol.userservice.api.feign.TilServiceFeignClient;
import propofol.userservice.api.member.controller.dto.MemberBoardsResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberBoardService {

    private final TilServiceFeignClient tilServiceFeignClient;

    public MemberBoardsResponseDto getMyBoards(Integer page, String token){
        return tilServiceFeignClient.getMyBoards(token, page).orElse(null);
    }
}
