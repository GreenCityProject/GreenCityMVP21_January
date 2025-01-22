package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "event")
@ToString(exclude = {"author", "comments", "initiativeTypes", "images"}) //we need to exclude fields that can create endless cycle, maybe we will add smth else
@EqualsAndHashCode(exclude = {"author", "comments", "initiativeTypes", "images"}) //the same
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private ZonedDateTime creationDate;

    @ManyToOne
    private User author;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventComment> comments = new ArrayList<>();

    @Column(name = "duration", nullable = false)
    private int duration;

    private int likes; //Maybe we will not need it when we add those fields connected with the User class

    @Column(name = "rating")
    private double rating;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventDateInfo> eventDays = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "event_initiative_type",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "initiative_type_id")
    )
    private List<InitiativeType> initiativeTypes = new ArrayList<>();

    @Column(name = "is_open")
    private boolean isOpen;

    @ManyToMany
    @JoinTable(
            name = "event_image",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private List<Image> images = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "event_participants",  // table for saving users joined to event
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants = new ArrayList<>();

    //We need one more field here - private Set<User> participants, but we can't add it without adding the field
    //private Set<Event> attending to the User class

    //We need two more fields here - private Set<User> usersLikedEvent and private Set<User> usersDislikedEvent,
    //but we need to create the field in the User class: private Set<Event> eventLiked and private Set<Event> eventDisliked

}
