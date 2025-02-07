package greencity.service;

import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.event.EventCommentRequestDto;
import greencity.dto.event.EventCommentResponseDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private EventRepo eventRepo;
    private EventCommentRepo eventCommentRepo;
    private ModelMapper modelMapper;
    private UserService userService;

    @Override
    public AddEventCommentDtoResponse addComment(Long eventId, Long userId, EventCommentRequestDto requestDto) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        UserVO userVO = userService.findById(userId);
        if (userVO == null) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        User user = modelMapper.map(userVO, User.class);

        EventComment eventComment = EventComment.builder()
                .text(requestDto.getText())
                .user(user)
                .event(event)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .deleted(false)
                .build();

        eventComment = eventCommentRepo.save(eventComment);

        return AddEventCommentDtoResponse.builder()
                .id(eventComment.getId())
                .text(eventComment.getText())
                .createdDate(eventComment.getCreatedDate())
                .modifiedDate(eventComment.getModifiedDate())
                .build();
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
    public EventCommentResponseDto getCommentById(Long commentId) {
        EventComment comment = eventCommentRepo.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        return modelMapper.map(comment, EventCommentResponseDto.class);
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
