package greencity.repository;

import greencity.entity.Event;
import greencity.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepo extends JpaRepository<Image, Long> {

    /**
     * Method to find an image by its path.
     *
     * @param imagePath the path of the image.
     * @return an {@link Optional} of {@link Image}.
     */
    Optional<Image> findByImagePath(String imagePath);

    /**
     * Method to find all images associated with a specific event.
     *
     * @param event the {@link Event} instance.
     * @return a list of {@link Image}.
     */
    @Query("SELECT i FROM Image i JOIN i.events e WHERE e = :event")
    List<Image> findAllByEvent(@Param("event") Event event);

}
