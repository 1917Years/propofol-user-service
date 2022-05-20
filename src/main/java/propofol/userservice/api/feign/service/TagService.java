package propofol.userservice.api.feign.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.userservice.api.feign.TagServiceFeignClient;
import propofol.userservice.api.feign.dto.TagNameDto;
import propofol.userservice.api.feign.dto.TagsDto;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagServiceFeignClient tagServiceFeignClient;

    public List<String> getTagNames(String token, List<Long> ids){
        TagsDto tagNames = tagServiceFeignClient.getTagNames(token, ids);
        List<TagNameDto> tags = tagNames.getTags();
        List<String> list = new ArrayList<>();

        tags.forEach(tag -> {
            list.add(tag.getName());
        });

        return list;
    }
}
