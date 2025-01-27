package greencity.mapping;

import greencity.dto.event.EventRequestDto;
import greencity.entity.Event;
import greencity.entity.InitiativeType;
import greencity.entity.Image;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EventRequestDtoMapper extends AbstractConverter<EventRequestDto, Event> {

    @Override
    protected Event convert(EventRequestDto eventRequestDto) {

        return Event.builder()
                .title(eventRequestDto.getTitle())
                .description(eventRequestDto.getDescription())
                .duration(eventRequestDto.getDuration())
                .initiativeTypes(eventRequestDto.getInitiativeTypes().stream()
                        .map(dto -> InitiativeType.builder()
                                .name(dto.getName())
                                .build())
                        .toList())
                .isOpen(eventRequestDto.isOpen())
                .images(eventRequestDto.getImages().stream()
                        .map(dto -> Image.builder()
                                .imagePath(dto.getImagePath())
                                .build())
                        .collect(Collectors.toSet()))
                .build();

    }

}
