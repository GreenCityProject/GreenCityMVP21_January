package greencity.service;

import greencity.dto.achievement.AchievementRequestDto;
import greencity.dto.achievement.AchievementResponseDto;

import java.util.List;

public interface AchievementService {

    AchievementResponseDto createAchievement(AchievementRequestDto achievementRequestDto);

    AchievementResponseDto getAchievementById(Long id);

    AchievementResponseDto getAchievementByType(String type);

    List<AchievementResponseDto> getAchievementsByRequiredRatesBetween(Integer startRate, Integer endRate);

    AchievementResponseDto getAchievementByRequiredRate(Integer requiredRate);

    List<AchievementResponseDto> getAchievementsByConditionsContainingText(String text);

    List<AchievementResponseDto> getAchievementsByTypeContainingText(String text);

    AchievementResponseDto updateAchievement(Long id, AchievementRequestDto achievementRequestDtoUpd);

    String deleteAchievement(Long id);
}
