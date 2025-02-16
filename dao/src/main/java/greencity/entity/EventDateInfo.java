package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_info")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class EventDateInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private Event event;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "num_of_day_in_event")
    private int numOfDayInEvent;

    @Column(name = "is_all_day")
    private boolean isAllDay;

    @Column(name = "event_time_start")
    private LocalDateTime eventTimeStart;

    @Column(name = "event_time_end")
    private LocalDateTime eventTimeEnd;

    @Column(name = "is_place", nullable = false)
    private boolean isPlace;

    @Column(name = "is_online", nullable = false)
    private boolean isOnline;

    @Column(name = "location")
    private String location;

    @Column(name = "url")
    private String url;

}
