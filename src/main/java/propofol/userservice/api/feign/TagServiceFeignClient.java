package propofol.userservice.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import propofol.userservice.api.feign.dto.TagsDto;

import java.util.Set;

@FeignClient("tag-service")
public interface TagServiceFeignClient {

    @GetMapping("api/v1/tags/ids")
    TagsDto getTagNames(@RequestHeader("Authorization") String token,
                        @RequestParam("ids") Set<Long> ids);
}
