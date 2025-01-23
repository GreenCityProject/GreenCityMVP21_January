package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_comment_likes")
@ToString(exclude = {"user", "comment"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventCommentLikes {
    @EmbeddedId
    private EventCommentLikesKey id;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @ManyToOne
    @MapsId("commentId")
    private EventComment comment;

    @Column
    private boolean isLiked;

    @Column
    private boolean isDisliked;
}
