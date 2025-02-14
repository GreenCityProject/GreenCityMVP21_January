package greencity.service;

import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.event.EventCommentRequestDto;
import greencity.dto.event.EventCommentResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EventCommentService {

    AddEventCommentDtoResponse addComment(Long eventId, Long userId, EventCommentRequestDto requestDto);

    AddEventCommentDtoResponse replyToComment(Long parentCommentId, Long userId, EventCommentRequestDto requestDto);

    void deleteComment(Long commentId);

    EventCommentResponseDto updateComment(Long commentId, EventCommentRequestDto requestDto);

    Page<EventCommentResponseDto> getCommentsByEvent(Long eventId, int page, int size);

    EventCommentResponseDto getCommentById(Long eventId, Long commentId);

    List<EventCommentResponseDto> getRepliesByComment(Long commentId);

    long countCommentsByEvent(Long eventId); // As i always said, we need to count somehow every like

    long countRepliesByComment(Long commentId); // so did replies

}
