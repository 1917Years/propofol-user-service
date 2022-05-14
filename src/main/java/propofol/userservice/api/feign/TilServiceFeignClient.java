package propofol.userservice.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import propofol.userservice.api.member.controller.dto.MemberBoardsResponseDto;

import java.util.Optional;

@FeignClient(name = "til-service")
public interface TilServiceFeignClient {
    @GetMapping("/api/v1/boards/myBoards")
    Optional<MemberBoardsResponseDto> getMyBoards(
            @RequestHeader(name = "Authorization", required = true) String token,
            @RequestParam(value = "page") Integer page);
}
