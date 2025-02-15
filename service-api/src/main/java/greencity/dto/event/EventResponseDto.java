package greencity.dto.event;

import greencity.dto.user.AuthorDto;
import greencity.dto.user.UserProfilePictureDto;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
public class EventResponseDto {
    private Long id;
    private String title;
    private String description;
    private int duration;
    private List<EventDateInfoResponseDto> eventDays;
    private List<InitiativeTypeResponseDto> initiativeTypes;
    private boolean isOpen;
    private List<ImageResponseDto> images;
    private AuthorDto author;
    private ZonedDateTime creationDate;
    private List<UserProfilePictureDto> participants;
    private ImageResponseDto mainImage;
    private double rating;
    private boolean isJoined;
    private int likes;
}
