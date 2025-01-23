package greencity.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class EventCommentLikesKey implements Serializable {
    private Long userId;
    private Long commentId;
}
