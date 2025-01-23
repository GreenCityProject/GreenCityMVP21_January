package greencity.repository;

import greencity.entity.EventComment;
import greencity.entity.EventCommentLike;
import greencity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventCommentLikeRepo extends JpaRepository<EventCommentLike, Long> {

    /**
     * Method to count the total number of likes for a specific comment.
     *
     * @param comment the {@link EventComment} instance.
     * @return total number of likes.
     */
    long countByComment(EventComment comment);

    /**
     * Method to check if a specific user liked a specific comment.
     *
     * @param user    the {@link User} instance.
     * @param comment the {@link EventComment} instance.
     * @return true if the user liked the comment, false otherwise.
     */
    boolean existsByUserAndComment(User user, EventComment comment);

}
