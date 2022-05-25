package propofol.userservice.api.member.controller.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MemberInfoAllResponseDto {
    List<MemberDetailInfoAllResponseDto> members = new ArrayList<>();
}
