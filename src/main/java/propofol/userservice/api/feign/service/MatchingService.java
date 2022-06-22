package propofol.userservice.api.feign.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.userservice.api.feign.MatchingServiceFeignClient;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MatchingServiceFeignClient matchingServiceFeignClient;

    public Set<Long> findAllBoardMember(Long boardId, String token){
        return matchingServiceFeignClient.findAllBoardMember(boardId, token);
    }
}
