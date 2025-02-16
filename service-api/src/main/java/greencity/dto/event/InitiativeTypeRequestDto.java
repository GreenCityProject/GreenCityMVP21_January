package greencity.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
public class InitiativeTypeRequestDto {
    @NotNull
    private String name;
}
