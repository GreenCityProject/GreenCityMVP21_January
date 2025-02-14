package greencity.dto.event;

import greencity.dto.user.UserProfilePictureDto;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class EventCommentResponseDto {
    private Long id;
    private String text;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private UserProfilePictureDto author;
    private int likes;
    private long parentCommentId;
}
