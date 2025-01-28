package greencity.dto.event;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventCommentLikesResponseDto {
    private Long userId;
    private Long eventCommentId;
    private boolean isLiked;
    private boolean isDisliked;
}
