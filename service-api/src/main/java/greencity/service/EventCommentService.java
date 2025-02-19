package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.event.EventCommentRequestDto;
import greencity.dto.event.EventCommentResponseDto;

import java.util.List;

public interface EventCommentService {

    AddEventCommentDtoResponse addComment(Long eventId, Long userId, EventCommentRequestDto requestDto);

    AddEventCommentDtoResponse replyToComment(Long parentCommentId, Long userId, EventCommentRequestDto requestDto);

    void deleteComment(Long commentId);

    EventCommentResponseDto updateComment(Long commentId, EventCommentRequestDto requestDto);

    PageableAdvancedDto<EventCommentResponseDto> getCommentsByEvent(Long eventId, int page, int size);

    EventCommentResponseDto getCommentById(Long eventId, Long commentId);

    List<EventCommentResponseDto> getRepliesByComment(Long commentId);

    long countCommentsByEvent(Long eventId);

    long countRepliesByComment(Long commentId);

}
