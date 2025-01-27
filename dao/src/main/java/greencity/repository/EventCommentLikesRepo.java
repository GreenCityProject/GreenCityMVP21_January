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

//    /**
//     * Method to count the total number of likes for a specific comment.
//     *
//     * @param comment the {@link EventComment} instance.
//     * @return total number of likes.
//     */
//    long countByComment(EventComment comment); //Causes problems

    //    /**
//     * Method to check if a specific user liked a specific comment.
//     *
//     * @param user    the {@link User} instance.
//     * @param comment the {@link EventComment} instance.
//     * @return true if the user liked the comment, false otherwise.
//     */
//    boolean existsByUserAndComment(User user, EventComment comment); // Causes problems

    @Query("SELECT e.id.user FROM EventCommentLikes e WHERE e.id.eventComment.id = :eventCommentId")
    List<User> findUsersByEventCommentId(@Param("eventCommentId") Long eventCommentId);

    @Query("SELECT e.id.eventComment FROM EventCommentLikes e WHERE e.id.user.id = :userId")
    List<EventComment> findEventCommentsByUserId(@Param("userId") Long userId);
}
