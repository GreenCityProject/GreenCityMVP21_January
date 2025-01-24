package greencity.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class EventRequestDto {
    @NotBlank(message = "Title cannot be empty")
    @Size(max = 70, message = "Event title must be up to 70 characters")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    @Size(
            min = 20,
            max = 63206,
            message = "Event description must contain more than 20 characters but less than 63,206 characters"
    )
    private String description;
    private int duration = 1;

    @Size(
            min = 1,
            max = 7,
            message = "The number of event days must be between 1 and 7"
    )
    private List<EventDateInfoRequestDto> eventDays;

    @Size(
            min = 1,
            max = 3,
            message = "The number of initiative types must be between 1 and 3"
    )
    private List<InitiativeTypeRequestDto> initiativeTypes;
    private boolean isOpen = true;
    private List<ImageRequestDto> images;
}
