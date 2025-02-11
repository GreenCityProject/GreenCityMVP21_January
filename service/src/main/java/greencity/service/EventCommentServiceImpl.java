package greencity.service;

import greencity.dto.event.AddEventCommentDtoRequest;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private EventRepo eventRepo;
    private EventCommentRepo eventCommentRepo;
    private ModelMapper modelMapper;
    private UserService userService;

    @Override
    public AddEventCommentDtoResponse addComment(Long eventId, Long userId, AddEventCommentDtoRequest requestDto) {
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

        List<User> mentionedUsers = extractMentionedUsers(requestDto.getText());

        return AddEventCommentDtoResponse.builder()
                .id(eventComment.getId())
                .text(eventComment.getText())
                .createdDate(eventComment.getCreatedDate())
                .modifiedDate(eventComment.getModifiedDate())
                .build();
    }

    private List<User> extractMentionedUsers(String text) {
        List<User> mentionedUsers = new ArrayList<>();

        Pattern pattern = Pattern.compile("[@#]([\\w\\s]+)");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String fullName = matcher.group(1).trim();

            Optional<UserVO> mentionedUserVO = userService.findByFullName(fullName);

            if (mentionedUserVO.isPresent()) {
                mentionedUsers.add(modelMapper.map(mentionedUserVO.get(), User.class));
            } else {
                throw new EntityNotFoundException("User with name " + fullName + " not found.");
            }
        }
        return mentionedUsers;
    }


    @Override
    public AddEventCommentDtoResponse replyToComment(Long parentCommentId, Long userId, EventCommentRequestDto requestDto) {
        EventComment parentComment = eventCommentRepo.findById(parentCommentId)
                .orElseThrow(() -> new EntityNotFoundException("Parent comment not found with id: " + parentCommentId));

        Event event = parentComment.getEvent();

        UserVO userVO = userService.findById(userId);
        if (userVO == null) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        User user = modelMapper.map(userVO, User.class);

        if (!event.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("Only the event organizer can reply to a comment.");
        }

        EventComment replyComment = EventComment.builder()
                .text(requestDto.getText())
                .user(user)
                .event(event)
                .parentComment(parentComment)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .deleted(false)
                .build();

        replyComment = eventCommentRepo.save(replyComment);

        return AddEventCommentDtoResponse.builder()
                .id(replyComment.getId())
                .text(replyComment.getText())
                .createdDate(replyComment.getCreatedDate())
                .modifiedDate(replyComment.getModifiedDate())
                .build();
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
