package greencity.service;

import greencity.dto.event.EventCommentRequestDto;
import greencity.dto.event.EventCommentResponseDto;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private EventRepo eventRepo;
    private EventCommentRepo eventCommentRepo;
    private ModelMapper modelMapper;

    @Override
    public EventCommentResponseDto addComment(Long eventId, Long userId, EventCommentRequestDto requestDto) {
        //This method is yet to be implemented
        return null;
    }

    @Override
    public EventCommentResponseDto replyToComment(Long parentCommentId, Long userId, EventCommentRequestDto requestDto) {
        //This method is yet to be implemented
        return null;
    }

    @Override
    public void deleteComment(Long commentId) {
        //This method is yet to be implemented
    }

    @Override
    public EventCommentResponseDto updateComment(Long commentId, EventCommentRequestDto requestDto) {
        //This method is yet to be implemented
        return null;
    }

    @Override
    public List<EventCommentResponseDto> getCommentsByEvent(Long eventId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        List<EventComment> eventComments = eventCommentRepo.findByEvent(event);

        return eventComments.stream()
                .map(eventComment -> modelMapper.map(eventComment, EventCommentResponseDto.class)).toList();
    }

    @Override
    public List<EventCommentResponseDto> getRepliesByComment(Long commentId) {
        //This method is yet to be implemented
        return List.of();
    }

    @Override
    public long countCommentsByEvent(Long eventId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        return eventCommentRepo.countByEvent(event);
    }

    @Override
    public long countRepliesByComment(Long commentId) {
        //This method is yet to be implemented
        return 0;
    }
}
