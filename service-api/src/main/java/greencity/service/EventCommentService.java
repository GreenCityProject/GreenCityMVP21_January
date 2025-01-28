package greencity.service;

import greencity.dto.event.EventCommentRequestDto;
import greencity.dto.event.EventCommentResponseDto;

import java.util.List;

public interface EventCommentService {

    EventCommentResponseDto addComment(Long eventId, Long userId, EventCommentRequestDto requestDto);

    EventCommentResponseDto replyToComment(Long parentCommentId, Long userId, EventCommentRequestDto requestDto);

    void deleteComment(Long commentId);

    EventCommentResponseDto updateComment(Long commentId, EventCommentRequestDto requestDto);

    List<EventCommentResponseDto> getCommentsByEvent(Long eventId);

    List<EventCommentResponseDto> getRepliesByComment(Long commentId);

    long countCommentsByEvent(Long eventId); // As i always said, we need to count somehow every like

    long countRepliesByComment(Long commentId); // so did replies

}
