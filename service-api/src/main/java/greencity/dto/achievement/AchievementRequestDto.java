package greencity.dto.achievement;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
public class AchievementRequestDto {

    @NotBlank
    private String type;

    @NotBlank
    private String conditions;

    @NotBlank
    private Integer requiredUserRating;
}
