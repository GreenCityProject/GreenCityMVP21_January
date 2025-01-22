package greencity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "event_comment")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
public class EventComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @Size(min = 1, max = 8000)
    private String text;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    @ManyToOne
    private EventComment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<EventComment> comments = new ArrayList<>();

    @ManyToOne
    private User user;

    @ManyToOne
    private Event event;

    @Column
    private boolean deleted;

    @Transient
    private boolean currentUserLiked = false;

    //We need one more field here - private Set<User> usersLiked - but we can't create it without adding
    //the field  private Set<EventComment> eventCommentsLiked; in the UserClass to uncouple the many-to-many connection.

}
