package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "initiative_type")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class InitiativeType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "initiativeTypes")
    private List<Event> events = new ArrayList<>();
}
