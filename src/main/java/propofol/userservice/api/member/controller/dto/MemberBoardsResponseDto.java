package propofol.userservice.api.member.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MemberBoardsResponseDto {
    private Integer totalPageCount;
    private Long totalCount;

    private List<BoardResponseDto> boards = new ArrayList<>();
}
