package greencity.repository;

import greencity.entity.User;
import greencity.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementRepo extends JpaRepository<UserAchievement, Long> {

    @Query("select ua from UserAchievement ua where ua.user.id = :userId and ua.achievement.id=:achievementId")
    Optional<UserAchievement> findByUserAndAchievement(Long userId, Long achievementId);

    List<UserAchievement> findByUser(User user);
}
