package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_likes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventLikes {
    @EmbeddedId
    private EventLikesKey id;

    @Column
    private boolean isLiked;

    @Column
    private boolean isDisliked;
}
