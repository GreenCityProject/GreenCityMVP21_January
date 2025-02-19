package greencity.repository;

import greencity.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepo extends JpaRepository<Achievement, Long> {

    Achievement findByRequiredRate(Integer requiredRate);

    @Query("select a from Achievement a where a.type=lower(:type) ")
    Achievement findByType(String type);

    @Query("select a from Achievement a where a.requiredRate between :startRate and :endRate")
    List<Achievement> findAllByRequiredRateBetween(Integer startRate, Integer endRate);

    @Query("SELECT a FROM Achievement a WHERE LOWER(CAST(a.type AS string)) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<Achievement> findByTypeContainingIgnoreCase(String text);

    List<Achievement> findByConditionsContainingIgnoreCase(String text);

}
