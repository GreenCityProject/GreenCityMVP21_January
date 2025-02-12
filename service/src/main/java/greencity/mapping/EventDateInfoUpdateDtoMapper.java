package greencity.mapping;

import greencity.dto.event.EventDateInfoRequestDto;
import greencity.dto.event.EventDateInfoUpdateDto;
import greencity.entity.EventDateInfo;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventDateInfoUpdateDtoMapper extends AbstractConverter<EventDateInfoUpdateDto, EventDateInfo> {
    @Override
    protected EventDateInfo convert(EventDateInfoUpdateDto dto) {
        return EventDateInfo.builder()
                .id(dto.getId())
                .eventDate(dto.getEventDate())
                .eventTimeStart(dto.getEventTimeStart())
                .eventTimeEnd(dto.getEventTimeEnd())
                .isAllDay(dto.getIsAllDay())
                .isPlace(dto.getIsPlace())
                .isOnline(dto.getIsOnline())
                .location(dto.getLocation())
                .url(dto.getUrl())
                .build();
    }
}
