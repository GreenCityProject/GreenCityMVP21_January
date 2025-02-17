package greencity.dto.userachievement;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
public class UserAchievementRequestDto {

    private Long userId;
    private Long achievementId;

}
