package greencity.dto.event;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ParticipationResponseDto {
    private Long userId;
    private Long eventId;
}
