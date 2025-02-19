package greencity.dto.achievement;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = "conditions")
@Builder
@ToString
public class AchievementResponseDto {

    private Long id;
    private String type;
    private String conditions;
    private Integer requiredRate;
}
