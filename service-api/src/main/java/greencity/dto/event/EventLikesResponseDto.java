package greencity.dto.event;

import lombok.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventLikesResponseDto {
    private Long userId;
    private Long eventId;
    private List<Long> userIds; // All users who liked the event
    private List<Long> eventIds; // All events liked by the user
    private boolean isLiked;
    private boolean isDisliked;
}
