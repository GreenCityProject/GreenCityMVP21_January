package greencity.dto.event;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class InitiativeTypeResponseDto {
    private Long id;
    private String name;
}
