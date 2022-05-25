package propofol.userservice.api.member.controller.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageResponseDto<T> {
    private Integer totalPageCount;
    private Long totalCount;

    private List<T> data = new ArrayList<>();
}
