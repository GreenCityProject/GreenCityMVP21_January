package greencity.repository;

import greencity.entity.EventComment;
import greencity.entity.EventCommentDislike;
import greencity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventCommentDislikeRepo extends JpaRepository<EventCommentDislike, Long> {

    /**
     * Method to count the total number of dislikes for a specific comment.
     *
     * @param comment the {@link EventComment} instance.
     * @return total number of dislikes.
     */
    long countByComment(EventComment comment);

    /**
     * Method to check if a specific user disliked a specific comment.
     *
     * @param user    the {@link User} instance.
     * @param comment the {@link EventComment} instance.
     * @return true if the user disliked the comment, false otherwise.
     */
    boolean existsByUserAndComment(User user, EventComment comment);

}
