package propofol.userservice.domain.member.entity;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
public class Member extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(updatable = false, nullable = false, unique = true)
    private String email; // 아이디
    @Column(nullable = false)
    private String password; // 비밀번호
    @Column(updatable = false)
    private String username; // 사용자 이름(성명)
    @Column(unique = true)
    private String nickname; // 별명
    private String phoneNumber;
    private LocalDate birth;
    private String degree; // 학력
    private String score; // 학점

    @Enumerated(value = EnumType.STRING)
    private Authority authority;

    @Builder(builderMethodName = "createMember")
    public Member(String email, String password, String username, String nickname,
                  String phoneNumber, LocalDate birth, String degree, String score, Authority authority) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.birth = birth;
        this.degree = degree;
        this.score = score;
        this.authority = authority;
    }

    public void update(String nickname, String degree, String score, String password, String phoneNumber){
        if(nickname != null) this.nickname = nickname;
        if(degree != null) this.degree = degree;
        if(score != null) this.score = score;
        if(password != null) this.password = password;
        if(phoneNumber != null) this.phoneNumber = phoneNumber;
    }

    public void updatePassword(String password){
        this.password = password;
    }

}
