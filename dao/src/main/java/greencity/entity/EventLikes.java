package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_likes")
@ToString(exclude = {"user", "event"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventLikes {
    @EmbeddedId
    private EventLikesKey id;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @ManyToOne
    @MapsId("eventId")
    private Event event;

    @Column
    private boolean isLiked;

    @Column
    private boolean isDisliked;
}
