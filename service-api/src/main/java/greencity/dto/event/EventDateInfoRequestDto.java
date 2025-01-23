package greencity.dto.event;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDateInfoRequestDto {

    @NotNull(message = "Event date is mandatory.")
    @FutureOrPresent(message = "Event date cannot be in the past.")
    private LocalDate eventDate;

    @Future(message = "Event start time must be in the future.")
    private LocalDateTime eventTimeStart;

    @Future(message = "Event end time must be in the future.")
    private LocalDateTime eventTimeEnd;

    @NotNull(message = "All day flag is mandatory.")
    private Boolean isAllDay;

    @NotNull(message = "Place flag is mandatory.")
    private Boolean isPlace;

    @NotNull(message = "Online flag is mandatory.")
    private Boolean isOnline;

    @Size(max = 255, message = "Location cannot exceed 255 characters.")
    @NotBlank(message = "Location is mandatory if the event is offline.")
    private String location;

    @Size(max = 2083, message = "URL length cannot exceed 2083 characters.")
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "Invalid URL format.")
    @NotBlank(message = "URL is mandatory if the event is online.")
    private String url;

}
