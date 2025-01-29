package greencity.service;

import greencity.dto.event.ParticipationRequestDto;
import greencity.dto.event.ParticipationResponseDto;
import greencity.entity.Event;
import greencity.entity.Participation;
import greencity.entity.ParticipationKey;
import greencity.entity.User;
import greencity.repository.EventRepo;
import greencity.repository.ParticipationRepo;
import greencity.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {
    private EventRepo eventRepo;
    private UserRepo userRepo;
    private ParticipationRepo participationRepo;
    private ModelMapper modelMapper;

    @Override
    public void addParticipation(ParticipationRequestDto participationRequestDto) {
        User user = userRepo.findById(participationRequestDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + participationRequestDto.getUserId()));
        Event event = eventRepo.findById(participationRequestDto.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + participationRequestDto.getEventId()));

        Participation participation = new Participation(new ParticipationKey(user, event));
        participationRepo.save(participation);
    }

    @Override
    public void removeParticipation(Long userId, Long eventId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        ParticipationKey participationKey = new ParticipationKey(user, event);
        Participation participation = participationRepo.findById(participationKey)
                .orElseThrow(() -> new EntityNotFoundException("Participation not found with id: " + participationKey));
        participationRepo.delete(participation);
    }

    @Override
    public List<ParticipationResponseDto> getParticipantsByEventId(Long eventId) {
        return List.of();
    }

    @Override
    public List<ParticipationResponseDto> getEventsByUserId(Long userId) {
        return List.of();
    }

    @Override
    public boolean isUserParticipating(Long userId, Long eventId) {
        return false;
    }
}
