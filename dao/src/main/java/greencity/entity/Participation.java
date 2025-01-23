package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "participation")
@ToString(exclude = {"user", "event"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Participation {
    @EmbeddedId
    private ParticipationKey id;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @ManyToOne
    @MapsId("eventId")
    private Event event;
}
