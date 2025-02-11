package greencity.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddEventCommentDtoRequest {
    @NotBlank(message = "Comment cannot be empty")
    @Size(min = 1, max = 8000)
    private String text;

    @NotNull
    private Long userId;
}

