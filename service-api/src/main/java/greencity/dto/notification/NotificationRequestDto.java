package greencity.dto.notification;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NotificationRequestDto {

    private Long senderId;
    private Long receiverId;
    private String message;
    private String description;
    private String section;
    private String linkToFollow;

}
