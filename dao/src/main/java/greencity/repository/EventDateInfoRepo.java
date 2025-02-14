package greencity.repository;

import greencity.entity.Event;
import greencity.entity.EventDateInfo;
import greencity.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventDateInfoRepo extends JpaRepository<EventDateInfo, Long> {
    /**
     * Method to find all date information for a specific event.
     *
     * @param event the {@link Event} instance.
     * @return list of {@link EventDateInfo} instances.
     */
    List<EventDateInfo> findByEvent(Event event);

    /**
     * Method to find all events happening on a specific date.
     *
     * @param eventDate the {@link LocalDate} instance.
     * @return list of {@link EventDateInfo} instances.
     */
    List<EventDateInfo> findByEventDate(LocalDate eventDate);

    /**
     * Method to find all events that are online.
     *
     * @return list of {@link EventDateInfo} instances.
     */
    List<EventDateInfo> findByIsOnlineTrue();

    /**
     * Method to find all events that are offline.
     *
     * @return list of {@link EventDateInfo} instances.
     */
    List<EventDateInfo> findByIsPlaceTrue();

    /**
     * Method to find all events that are happening at a specific location.
     *
     * @param location the location as a string.
     * @return list of {@link EventDateInfo} instances.
     */
    List<EventDateInfo> findByLocation(String location);

    /**
     * Method to find all events that are happening on a specific url.
     *
     * @param url the url as a string.
     * @return list of {@link EventDateInfo} instances.
     */
    List<EventDateInfo> findByUrl(String url);

    /**
     * Method to find all events that are happening within a specific date range.
     *
     * @param startDate the start date of the range.
     * @param endDate   the end date of the range.
     * @return list of {@link EventDateInfo} instances.
     */
    @Query("SELECT edi FROM EventDateInfo edi WHERE edi.eventDate BETWEEN :startDate AND :endDate")
    List<EventDateInfo> findByEventDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Method to find all all-day events.
     *
     * @return list of {@link EventDateInfo} instances.
     */
    List<EventDateInfo> findByIsAllDayTrue();

    /**
     * Method to get a EventDateInfo by its ID.
     *
     * @param id ID of the EventDateInfo.
     * @return an {@link Optional} of {@link EventDateInfo}.
     */
    Optional<EventDateInfo> findById(Long id);
}
