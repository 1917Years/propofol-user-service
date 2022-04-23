package propofol.userservice.domain.member.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(updatable = false, nullable = false, unique = true)
    private String email; // 아이디
    @Column(nullable = false)
    private String password; // 비밀번호
    @Column(nullable = false, updatable = false)
    private String username; // 사용자 이름(성명)
    @Column(nullable = false)
    private String nickName; // 별명
    @Column(nullable = false)
    private String phoneNumber;
    private LocalDate birth;
    private String degree; // 학력
    private String score; // 학점

    @Enumerated(value = EnumType.STRING)
    private MemberRole memberRole;

    @Builder(builderMethodName = "createMember")
    public Member(String email, String password, String username, String nickName,
                  String phoneNumber, LocalDate birth, String degree, String score, MemberRole memberRole) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
        this.birth = birth;
        this.degree = degree;
        this.score = score;
        this.memberRole = memberRole;
    }
}
