package greencity.mapping;

import greencity.dto.event.EventCommentRequestDto;
import greencity.entity.EventComment;
import org.modelmapper.AbstractConverter;

import java.time.LocalDateTime;

public class EventCommentRequestDtoMapper extends AbstractConverter<EventCommentRequestDto, EventComment> {
    @Override
    protected EventComment convert(EventCommentRequestDto eventCommentRequestDto) {
        if (eventCommentRequestDto == null) {
            return null;
        }

        return EventComment.builder()
                .text(eventCommentRequestDto.getText())
                .createdDate(LocalDateTime.now())
                .build();
    }
}
