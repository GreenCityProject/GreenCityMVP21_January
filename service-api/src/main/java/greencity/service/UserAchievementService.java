package greencity.service;

import greencity.dto.achievement.AchievementResponseDto;
import greencity.dto.userachievement.UserAchievementRequestDto;
import greencity.dto.userachievement.UserAchievementResponseDto;

import java.util.List;

public interface UserAchievementService {

    UserAchievementResponseDto addUserAchievement(UserAchievementRequestDto userAchievementRequestDto);

    List<UserAchievementResponseDto> getAllUserAchievements(Long userId);

    UserAchievementResponseDto getUserAchievementByIds(UserAchievementRequestDto userAchievementRequestDto);
}
