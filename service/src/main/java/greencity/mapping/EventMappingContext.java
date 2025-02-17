package greencity.mapping;

import greencity.entity.Event;
import greencity.entity.EventDateInfo;
import greencity.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class EventMappingContext {
    private final Event event;
    private final EventDateInfo eventDateInfo;
    private final List<User> participants;
}
