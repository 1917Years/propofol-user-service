package propofol.userservice.api.member.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoDto {
    private Long id;
    private String nickName;
    private String profileString;
    private String profileType;

    private List<TagDetailDto> tags = new ArrayList<>();
}
