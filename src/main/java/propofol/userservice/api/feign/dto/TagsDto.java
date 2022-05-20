package propofol.userservice.api.feign.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TagsDto {
    private List<TagNameDto> tags = new ArrayList<>();
}
