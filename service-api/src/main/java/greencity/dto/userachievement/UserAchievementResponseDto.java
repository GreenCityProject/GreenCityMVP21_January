package greencity.dto.userachievement;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
public class UserAchievementResponseDto {

    private Long id;
    private Long userId;
    private Long achievementId;
    private String achievementType;
    private String achievementDate;
    private Boolean isActive;

}
