package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_comment_likes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventCommentLikes {
    @EmbeddedId
    private EventCommentLikesKey id;

    @Column
    private boolean isLiked;

    @Column
    private boolean isDisliked;
}
