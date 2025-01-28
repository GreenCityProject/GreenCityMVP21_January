package greencity.repository;

import greencity.entity.EventComment;
import greencity.entity.EventCommentLikes;
import greencity.entity.EventCommentLikesKey;
import greencity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventCommentLikesRepo extends JpaRepository<EventCommentLikes, EventCommentLikesKey> {

    @Query("SELECT e.id.user FROM EventCommentLikes e WHERE e.id.eventComment.id = :eventCommentId")
    List<User> findUsersByEventCommentId(@Param("eventCommentId") Long eventCommentId);

    @Query("SELECT e.id.eventComment FROM EventCommentLikes e WHERE e.id.user.id = :userId")
    List<EventComment> findEventCommentsByUserId(@Param("userId") Long userId);

}
