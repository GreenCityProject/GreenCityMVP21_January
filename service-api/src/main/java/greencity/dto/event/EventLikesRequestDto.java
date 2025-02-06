package greencity.dto.event;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventLikesRequestDto {
    private Long userId;
    private Long eventId;
    private boolean isLiked;
    private boolean isDisliked;
}