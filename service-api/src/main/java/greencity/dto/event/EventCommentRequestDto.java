package greencity.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class EventCommentRequestDto {
    @NotBlank(message = "Comment cannot be empty")
    @Size(min = 1, max = 8000)
    private String text;
}
