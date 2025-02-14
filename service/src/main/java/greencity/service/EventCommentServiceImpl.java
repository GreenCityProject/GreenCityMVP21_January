package greencity.service;

import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.event.EventCommentRequestDto;
import greencity.dto.event.EventCommentResponseDto;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final EventRepo eventRepo;
    private final EventCommentRepo eventCommentRepo;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final NotificationService notificationService;

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

        if (!event.getAuthor().equals(user) && !event.getAuthor().getId().equals(user.getId())) {
            notificationService.addNotification(new NotificationRequestDto(
                    user.getId(),
                    event.getAuthor().getId(),
                    "New comment on your event: " + event.getTitle(),
                    "A new comment has been added to your event.",
                    "GreenCity",
                    "/events/" + eventId));
        }

        List<User> mentionedUsers = extractMentionedUsers(requestDto.getText());
        for (User mentionedUser : mentionedUsers) {
            if (!mentionedUser.getId().equals(user.getId())) {
                notificationService.addNotification(new NotificationRequestDto(
                        user.getId(),
                        mentionedUser.getId(),
                        "You were mentioned in a comment on: " + event.getTitle(),
                        "Someone mentioned you in a comment.",
                        "GreenCity",
                        "/events/" + eventId));
            }
        }



        return AddEventCommentDtoResponse.builder()
                .id(eventComment.getId())
                .text(eventComment.getText())
                .createdDate(eventComment.getCreatedDate())
                .modifiedDate(eventComment.getModifiedDate())
                .build();
    }

    private List<User> extractMentionedUsers(String text) {
        List<User> mentionedUsers = new ArrayList<>();

        Pattern pattern = Pattern.compile("[@#]([\\w_]+)");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String taggedName = matcher.group(1).trim();

            String fullName = taggedName.replace("_", " ");

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

        if (!parentComment.getUser().equals(user)) {
            notificationService.addNotification(new NotificationRequestDto(
                    user.getId(),
                    parentComment.getUser().getId(),
                    "Someone replied to your comment on: " + event.getTitle(),
                    "You received a reply to your comment.",
                    "GreenCity",
                    "/events/" + event.getId()));
        }

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
    public Page<EventCommentResponseDto> getCommentsByEvent(Long eventId, int page, int size) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        Pageable pageable = PageRequest.of(page, size);
        Page<EventComment> eventComments = eventCommentRepo.findByEvent(event, pageable);

        return eventComments.map(comment -> modelMapper.map(comment, EventCommentResponseDto.class));
    }

    @Override
    public EventCommentResponseDto getCommentById(Long eventId, Long commentId) {
        EventComment comment = eventCommentRepo.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        if (!comment.getEvent().getId().equals(eventId)) {
            throw new EntityNotFoundException("Comment with id " + commentId + " does not belong to event " + eventId);
        }

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
