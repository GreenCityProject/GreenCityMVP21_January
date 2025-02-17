package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.friendship.FriendCardDto;
import greencity.dto.friendship.FriendshipVO;
import greencity.dto.friendship.RequestedFriendshipDto;
import greencity.dto.notification.NotificationRequestDto;
import greencity.entity.Friendship;
import greencity.entity.User;
import greencity.enums.FriendshipStatus;
import greencity.enums.NotificationSection;
import greencity.repository.FriendshipRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepo friendshipRepo;
    private final UserRepo userRepo;
    private final HabitAssignRepo habitAssignRepo;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;
    private final Long ONE_WEEK = 1L;
    private final Function<Friendship, Long> getFriendId = friendship -> friendship.getFriend().getId();

    @Autowired
    public FriendshipServiceImpl(
            FriendshipRepo friendshipRepo,
            UserRepo userRepo,
            HabitAssignRepo habitAssignRepo,
            NotificationService notificationService,
            ModelMapper modelMapper) {
        this.friendshipRepo = friendshipRepo;
        this.userRepo = userRepo;
        this.habitAssignRepo = habitAssignRepo;
        this.notificationService = notificationService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<FriendCardDto> getAllMutualFriendsByUserId(Long userId, Long targetUserId) {
        List<FriendCardDto> userFriends = new ArrayList<>(getAllFriendCardsOfUserById(userId));
        List<FriendCardDto> targetUserFriends = new ArrayList<>(getAllFriendCardsOfUserById(targetUserId));
        return (userFriends.retainAll(targetUserFriends)) ? userFriends : new ArrayList<>();
    }

    // TODO  IDK what is this requirement for - System should recommend friends based on the following criteria: d. replies
    @Override
    public PageableDto<FriendCardDto> recommendFriendsForUser(Pageable pageable, Long userId) {
        List<FriendCardDto> recommendedFriendsMeetingCriteria = Stream.of(
                getPotentialFriendsFromTheSameCity(userId),
                getPotentialFriendsWithTheSameHabits(userId),
                getPotentialFriendsWithCommonFriendsInStatus(userId, FriendshipStatus.ACCEPTED))
                    .flatMap(List::stream)
                    .toList();

        if(recommendedFriendsMeetingCriteria.isEmpty()) {
            recommendedFriendsMeetingCriteria = userRepo.findAll(pageable).stream()
                    .map(friend -> modelMapper.map(friend, FriendCardDto.class))
                    .toList();
        }

        List<FriendCardDto> recommendedFriends = recommendedFriendsMeetingCriteria.stream()
                .peek(friendCard ->
                    friendCard.setMutualFriends(getAmountOfMutualFriends(userId, friendCard.getId())))
                .filter(friendCard ->
                        !friendCard.getId().equals(userId) && !areFriends(userId, friendCard.getId()))
                .distinct()
                .toList();

        final int CURRENT_PAGE = 0;
        final int PAGE_SIZE = 12;
        int TOTAL_PAGES = (Long.valueOf(recommendedFriends.size()).intValue() + PAGE_SIZE - 1) / PAGE_SIZE;

        return new PageableDto<>(
                recommendedFriends,
                recommendedFriends.size(),
                CURRENT_PAGE,
                TOTAL_PAGES
        );
    }

    @Override
    public List<FriendCardDto> getAllFriendsByUserId(Long id) {
        return getAllFriendCardsOfUserById(id);
    }



    @Override
    public boolean requestFriendshipByUserId(Long senderId, Long recipientId) {
        Optional<Friendship> friendshipOptional = getFriendshipByUserIdOrderInsensitive(senderId, recipientId);
        Friendship friendship = friendshipOptional.orElseGet(Friendship::new);
        boolean isNotEligibleForResending = friendshipOptional.isPresent()
                                            && friendshipOptional.get().getStatus() != FriendshipStatus.CANCELLED
                                            && friendshipOptional.get().getStatus() != FriendshipStatus.DECLINED;

        if (isNotEligibleForResending || senderId.equals(recipientId)) {
            return false;
        } else if (friendshipOptional.isEmpty()) {
            friendship.setUser(userRepo.getReferenceById(senderId));
            friendship.setFriend(userRepo.getReferenceById(recipientId));
        }

        friendship.setStatus(FriendshipStatus.REQUESTED);
        friendship.setRequestedAt(LocalDateTime.now());
        friendship.setFriendshipRequestExpiration(LocalDateTime.now().plusWeeks(ONE_WEEK));
        friendshipRepo.save(friendship);
        sendNotification(senderId, recipientId, "New Friendship Req.");
        return true;
    }


    @Override
    public boolean cancelFriendshipRequestByUserId(Long senderId, Long recipientId) {
        Optional<Friendship> friendshipOptional = getFriendshipByUserIdOrderInsensitive(senderId, recipientId);
        Friendship friendship = friendshipOptional.orElseGet(Friendship::new);
        if (friendshipOptional.isPresent() && friendship.getStatus() == FriendshipStatus.REQUESTED) {
            friendship.setStatus(FriendshipStatus.CANCELLED);
            friendshipRepo.save(friendship);
            sendNotification(senderId, recipientId, "Cancel Friendship Req.");
            return true;
        }
        return false;
    }

    @Override
    public boolean acceptFriendshipRequestByUserId(Long senderId, Long recipientId) {
        Optional<Friendship> friendshipOptional = getFriendshipByUserIdOrderInsensitive(senderId, recipientId);
        Friendship friendship = friendshipOptional.orElseGet(Friendship::new);
        if (friendshipOptional.isPresent() && friendship.getStatus() == FriendshipStatus.REQUESTED) {
            friendship.setStatus(FriendshipStatus.ACCEPTED);
            friendshipRepo.save(friendship);
            sendNotification(senderId, recipientId, "Accepted Friendship Req.");
            return true;
        }
        return false;
    }

    @Override
    public boolean declineFriendshipRequestByUserId(Long senderId, Long recipientId) {
        Optional<Friendship> friendshipOptional = getFriendshipByUserIdOrderInsensitive(senderId, recipientId);
        Friendship friendship = friendshipOptional.orElseGet(Friendship::new);
        if (friendshipOptional.isPresent() && friendship.getStatus() == FriendshipStatus.REQUESTED) {
            friendship.setStatus(FriendshipStatus.DECLINED);
            friendshipRepo.save(friendship);
            sendNotification(senderId, recipientId, "Declined Friendship Req.");
            return true;
        }
        return false;
    }

    @Override
    public Optional<FriendshipStatus> getFriendshipStatusByUserId(Long userId, Long targetUserId) {
        return getFriendshipByUserIdOrderInsensitive(userId, targetUserId).map(Friendship::getStatus);
    }

    @Override
    public boolean deleteFriendByUserId(Long userId, Long friendId) {
        Optional<Friendship> friendshipOptional = getFriendshipByUserIdOrderInsensitive(userId, friendId);
        if (friendshipOptional.isPresent() && friendshipOptional.get().getStatus() == FriendshipStatus.ACCEPTED) {
            friendshipRepo.delete(friendshipOptional.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean areFriends(Long userId, Long friendId) {
        Optional<Friendship> friendshipOptional = getFriendshipByUserIdOrderInsensitive(userId, friendId);
        return friendshipOptional.isPresent() && friendshipOptional.get().getStatus() == FriendshipStatus.ACCEPTED;
    }


    @Override
    public boolean blockFriendshipRequestsFromUserById(Long senderId, Long recipientId) {
        Optional<Friendship> friendshipOptional = getFriendshipByUserIdOrderInsensitive(senderId, recipientId);
        Friendship friendship = friendshipOptional.orElseGet(Friendship::new);
        if (friendshipOptional.isPresent() && friendship.getStatus() == FriendshipStatus.REQUESTED) {
            friendship.setStatus(FriendshipStatus.BLOCKED);
            friendshipRepo.save(friendship);
            sendNotification(senderId, recipientId, "Blocked Friendship Req.");
            return true;
        }
        return false;
    }


    @Override
    public Optional<FriendshipVO> findFriendshipByParticipantsId(Long userId, Long friendId) {
        Optional<Friendship> friendship = friendshipRepo.findFriendshipByEitherUserId(userId, friendId);
        return friendship.map(f -> modelMapper.map(f, FriendshipVO.class));
    }

    @Override
    public Optional<FriendshipVO> findFriendshipById(Long friendshipId) {
        Optional<Friendship> friendship = friendshipRepo.findFriendshipById(friendshipId);
        return friendship.map(f -> modelMapper.map(f, FriendshipVO.class));
    }

    @Override
    public List<RequestedFriendshipDto> getAllFriendshipRequestsForUserById(Long recipientId) {
        return friendshipRepo.getFriendshipRequestsByUserId(recipientId).stream()
                .map(friendship -> modelMapper.map(friendship, RequestedFriendshipDto.class))
                .toList();
    }

    private Optional<Friendship> getFriendshipByUserIdOrderInsensitive(Long userId, Long friendId) {
        return friendshipRepo.findFriendshipByEitherUserId(userId, friendId);
    }

    private List<FriendCardDto> getAllFriendCardsOfUserById(Long userId) {
        return friendshipRepo.getAllFriendshipsByUserId(userId).stream()
                .map(friendship -> {
                    FriendCardDto friendCard = modelMapper.map(friendship.getFriend(), FriendCardDto.class);
                    friendCard.setMutualFriends(getAmountOfMutualFriends(userId, getFriendId.apply(friendship)));
                    friendCard.setFriendshipId(Optional.ofNullable(friendship.getId()));
                    return friendCard;
                })
                .toList();
    }

    private int getAmountOfMutualFriends(Long userId, Long targetUserId) {
        List<Long> userFriends =
                new ArrayList<>(friendshipRepo.getAllFriendshipsByUserId(userId).stream().map(getFriendId).toList());
        List<Long> targetFriends =
                new ArrayList<>(friendshipRepo.getAllFriendshipsByUserId(targetUserId).stream().map(getFriendId).toList());
        userFriends.retainAll(targetFriends);
        return userFriends.size();
    }

    private List<FriendCardDto> getPotentialFriendsFromTheSameCity(Long userId) {
        Optional<User> user = userRepo.findById(userId);

        List<FriendCardDto> result = new ArrayList<>();
        if(user.isPresent() && Objects.nonNull(user.get().getCity())) {
            result = userRepo.findAllByCity(user.get().getCity()).stream()
                    .map(friend -> modelMapper.map(friend, FriendCardDto.class))
                    .filter(friendCard -> !friendCard.getId().equals(userId))
                    .toList();
        }

        return  result;
    }

    private List<FriendCardDto> getPotentialFriendsWithTheSameHabits(Long userId) {
        return  Stream.of(
                        habitAssignRepo.findAllByUserIdAndStatusAcquired(userId),
                        habitAssignRepo.findAllByUserIdAndStatusIsInProgress(userId))
                .flatMap(List::stream)
                .flatMap(habit -> habitAssignRepo.findUserIdsByHabitId(habit.getId()).stream()
                        .map(userRepo::findById)
                        .filter(Optional::isPresent)
                        .map(optionalUser -> modelMapper.map(optionalUser.get(), FriendCardDto.class)))
                        .filter(friendCard -> !friendCard.getId().equals(userId))
                .toList();
    }

    private List<FriendCardDto> getPotentialFriendsWithCommonFriendsInStatus(Long userId, FriendshipStatus status) {
        return friendshipRepo.findUsersWithCommonFriendsInStatus(userId, status).stream()
                .map(friend -> modelMapper.map(friend, FriendCardDto.class))
                .toList();
    }

    private void sendNotification(Long senderId, Long receiverId, String message) {
        NotificationRequestDto notification = NotificationRequestDto.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .message(message)
                .section(NotificationSection.GreenCity.name())
                .build();
        notificationService.addNotification(notification);
    }
}
