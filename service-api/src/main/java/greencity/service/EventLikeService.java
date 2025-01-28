package greencity.service;

import greencity.dto.event.EventLikesRequestDto;
import greencity.dto.event.EventLikesResponseDto;

import java.util.List;

public interface EventLikeService {

    void addLike(EventLikesRequestDto eventLikesRequestDto);

    void removeLike(Long userId, Long eventId);

    EventLikesResponseDto getLikesByEventId(Long eventId);

    EventLikesResponseDto getLikesByUserId(Long userId);

    List<EventLikesResponseDto> getAllLikes();

}
