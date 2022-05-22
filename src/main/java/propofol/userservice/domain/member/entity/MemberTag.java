package propofol.userservice.domain.member.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_tag")
public class MemberTag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_tag_id")
    private Long id;

    private Long TagId;
    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", updatable = false)
    private Member member;

    public void changeMember(Member member) {this.member = member;}
    public void changeCount(int count) {this.count = count;}

    @Builder(builderMethodName = "createTag")
    public MemberTag(Long tagId, int count) {
        this.TagId = tagId;
        this.count = count;
    }
}
