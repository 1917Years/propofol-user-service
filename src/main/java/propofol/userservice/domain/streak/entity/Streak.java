package propofol.userservice.domain.streak.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import propofol.userservice.domain.member.entity.Member;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Streak{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "streak_id")
    private Long id;

    private LocalDate workingDate;
    private Integer working;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void addMember(Member member){ this.member = member; }

    public void addWorking(int working) { this.working = working; }
    @Builder(builderMethodName = "createStreak")
    public Streak(LocalDate workingDate, Integer working) {
        this.workingDate = workingDate;
        this.working = working;
    }
}
