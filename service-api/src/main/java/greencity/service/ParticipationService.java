package greencity.service;

import greencity.dto.event.ParticipationRequestDto;
import greencity.dto.event.ParticipationResponseDto;

import java.util.List;

public interface ParticipationService {

    void addParticipation(ParticipationRequestDto participationRequestDto);

    void removeParticipation(Long userId, Long eventId);

    List<ParticipationResponseDto> getParticipantsByEventId(Long eventId);

    List<ParticipationResponseDto> getEventsByUserId(Long userId);

    boolean isUserParticipating(Long userId, Long eventId);
}
