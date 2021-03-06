package propofol.userservice.api.member.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class BoardResponseDto {
    private Long id;
    private String title;
    private String content;
    private byte[] imageBytes;
    private String imageType;
    private Integer recommend;
    private Integer commentCount;
    private Boolean open;
    private LocalDateTime createdDate;
    private List<TagResponseDto> tags = new ArrayList<>();
}
