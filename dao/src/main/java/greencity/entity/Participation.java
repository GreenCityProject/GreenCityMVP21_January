package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "participation")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Participation {
    @EmbeddedId
    private ParticipationKey id;
}
