package greencity.dto.event;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class EventCommentRequestDto {
    @NotBlank(message = "Comment cannot be empty")
    private String text;
}
