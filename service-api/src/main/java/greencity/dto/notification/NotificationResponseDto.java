package greencity.dto.notification;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NotificationResponseDto {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String message;
    private String createdAt;
    private Boolean isViewed;
    private String description;
    private String section;
    private String linkToFollow;

}
