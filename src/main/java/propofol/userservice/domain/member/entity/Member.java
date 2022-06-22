package propofol.userservice.domain.member.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import propofol.userservice.domain.image.entity.Profile;
import propofol.userservice.domain.timetable.entity.TimeTable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    private long totalRecommend;

    @Enumerated(value = EnumType.STRING)
    private Authority authority;

    private String refreshToken;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "member")
    List<TimeTable> timeTables = new ArrayList<>();

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "member")
    List<Profile> profile = new ArrayList<>();

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    List<MemberTag> memberTags = new ArrayList<>();

    public void plusTotalRecommend() {this.totalRecommend = totalRecommend + 1;}
    public void changeRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    @Builder(builderMethodName = "createMember")
    public Member(String email, String password, String username, String nickname, String phoneNumber,
                  Authority authority, long totalRecommend) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.authority = authority;
        this.totalRecommend = totalRecommend;
    }

    public void update(String nickname, String password, String phoneNumber){
        if(nickname != null) this.nickname = nickname;
        if(password != null) this.password = password;
        if(phoneNumber != null) this.phoneNumber = phoneNumber;
    }

    public void updatePassword(String password){
        this.password = password;
    }

}
