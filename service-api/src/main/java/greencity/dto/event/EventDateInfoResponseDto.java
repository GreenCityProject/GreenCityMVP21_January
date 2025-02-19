package greencity.dto.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EventDateInfoResponseDto {

    private Long id;
    private LocalDate eventDate;
    private LocalDateTime eventTimeStart;
    private LocalDateTime eventTimeEnd;
    private boolean isAllDay;
    private boolean isPlace;
    private boolean isOnline;
    private String location;
    private String url;
    private int numOfDayInEvent;

}
