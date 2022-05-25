package propofol.userservice.api.member.controller.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MatchingResponseDto {
    private Integer totalPageCount;
    private Long totalCount;
    private List<MatchingDetailResponseDto> data = new ArrayList<>();
}
