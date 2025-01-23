package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_like")
@ToString(exclude = {"user", "event"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventLikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Event event;

    @Column
    private boolean isLiked;

    @Column
    private boolean isDisliked;
}
