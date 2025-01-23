package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_dislike")
@ToString(exclude = {"user", "event"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventDislike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Event event;
}
