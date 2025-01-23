package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_comment_like")
@ToString(exclude = {"user", "comment"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventCommentLikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private EventComment comment;

    @Column
    private boolean isLiked;

    @Column
    private boolean isDisliked;
}
