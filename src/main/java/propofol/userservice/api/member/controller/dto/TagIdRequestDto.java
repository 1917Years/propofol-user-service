package propofol.userservice.api.member.controller.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TagIdRequestDto {
    private List<Long> tagIds = new ArrayList<>();
}
