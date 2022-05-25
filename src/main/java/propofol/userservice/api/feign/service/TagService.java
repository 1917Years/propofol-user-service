package propofol.userservice.api.feign.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.userservice.api.feign.TagServiceFeignClient;
import propofol.userservice.api.feign.dto.TagsDto;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagServiceFeignClient tagServiceFeignClient;

    public TagsDto getTagNames(String token, Set<Long> ids){
        return tagServiceFeignClient.getTagNames(token, ids);
    }
}
