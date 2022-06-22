package propofol.userservice.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Set;

@FeignClient("matching-service")
public interface MatchingServiceFeignClient {

    @GetMapping("/api/v1/members/{boardId}/allMemberId")
    Set<Long> findAllBoardMember(@PathVariable("boardId") Long boardId,
                                 @RequestHeader(value = "Authorization", required = false) String token);
}
