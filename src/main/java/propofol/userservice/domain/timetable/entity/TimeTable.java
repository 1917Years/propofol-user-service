package propofol.userservice.domain.timetable.entity;

import lombok.*;
import propofol.userservice.domain.member.entity.Member;

import javax.persistence.*;
import java.time.LocalTime;

@Getter
@Entity @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeTable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_id")
    private Long id;

    private String week; // 요일
    @Column(columnDefinition = "TIME")
    private LocalTime startTime;
    @Column(columnDefinition = "TIME")
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder(builderMethodName = "createTimeTable")
    public TimeTable(String week, LocalTime startTime, LocalTime endTime) {
        this.week = week;
        this.startTime = startTime;
        this.endTime = endTime;
    }



    public void changeMember(Member findMember) {
        this.member = findMember;
    }
}
