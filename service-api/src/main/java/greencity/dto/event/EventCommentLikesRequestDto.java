package greencity.dto.event;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventCommentLikesRequestDto {
    private boolean isLiked;
    private boolean isDisliked;
}
