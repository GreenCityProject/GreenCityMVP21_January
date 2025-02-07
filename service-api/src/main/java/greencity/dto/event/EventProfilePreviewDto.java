package greencity.dto.event;

import greencity.dto.user.AuthorDto;
import greencity.dto.user.UserProfilePictureDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class EventProfilePreviewDto {
    private Long id;
    private String title;
    private ZonedDateTime creationDate;
    private LocalDate eventDate;
    private LocalDateTime eventTimeStart;
    private AuthorDto author;
    private String location;
    private List<InitiativeTypeResponseDto> initiativeTypes;
    private boolean isOpen;
    private ImageResponseDto mainImage;
    private double rating;
    private List<UserProfilePictureDto> participants;
}
