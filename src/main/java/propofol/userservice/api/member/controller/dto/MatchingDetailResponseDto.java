package propofol.userservice.api.member.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class MatchingDetailResponseDto {
    private Long id;
    private String nickname;
    private long totalRecommend; // TIL 총 추천 수
    private String profileString;
    private String profileType;

    private List<TagDetailDto> tagData = new ArrayList<>();
    private List<TimeTableDetailDto> timetableData = new ArrayList<>();
}
