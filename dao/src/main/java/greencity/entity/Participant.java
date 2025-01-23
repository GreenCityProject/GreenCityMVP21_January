package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "participant")
@ToString(exclude = {"user", "event"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Event event;
}
