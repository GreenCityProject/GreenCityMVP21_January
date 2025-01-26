package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.friendship.FriendCardDto;
import greencity.dto.friendship.FriendshipRequestDto;
import greencity.dto.user.UserVO;
import greencity.entity.Friendship;
import greencity.enums.FriendshipStatus;
import greencity.repository.FriendshipRepo;
import greencity.repository.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepo friendshipRepo;
    private final UserRepo userRepo;
    private final NotificationServise notificationServise;

    @Autowired
    public FriendshipServiceImpl(
            FriendshipRepo friendshipRepo,
            UserRepo userRepo,
            NotificationServise notificationServise,
            ModelMapper modelMapper) {
        this.friendshipRepo = friendshipRepo;
        this.userRepo = userRepo;
        this.notificationServise = notificationServise;
        this.modelMapper = modelMapper;
    }

    private final ModelMapper modelMapper;

    private final Long ONE_WEEK = 1L;


    @Override
    public List<FriendCardDto> getAllFriendsByUserId(Long id) {
        return getAllFriendsOfUserById(id);
    }

    @Override
    public List<FriendCardDto> getAllMutualFriendsByUserId(Long userId, Long targetUserId) {
        List<FriendCardDto> userFriends = getAllFriendsByUserId(userId);
        List<FriendCardDto> targetUserFriends = getAllFriendsByUserId(targetUserId);
        return (userFriends.retainAll(targetUserFriends)) ? userFriends : new ArrayList<>();
    }

    @Override
    // TODO (check if proposed are not in pending status or already befriended)
    public PageableDto<FriendCardDto> recommendFriendsForUser(UserVO user) {
        return null;
    }

    @Override
    public boolean requestFriendshipByUserId(Long senderId, Long recipientId) {
        Optional<Friendship> friendshipOptional = getFriendshipByUserIdOrderInsensitive(senderId, recipientId);
        Friendship friendship = friendshipOptional.orElseGet(Friendship::new);
        boolean isNotEligibleForResending = friendshipOptional.isPresent()
                && friendshipOptional.get().getStatus() != FriendshipStatus.CANCELLED
                && friendshipOptional.get().getStatus() != FriendshipStatus.DECLINED;

        if (isNotEligibleForResending) {
            return false;
        } else if (friendshipOptional.isEmpty()) {
            friendship.setUser(userRepo.getReferenceById(senderId));
            friendship.setFriend(userRepo.getReferenceById(recipientId));
        }

        friendship.setStatus(FriendshipStatus.REQUESTED);
        friendship.setRequestedAt(LocalDateTime.now());
        friendship.setFriendshipRequestExpiration(LocalDateTime.now().plusWeeks(ONE_WEEK));
        friendshipRepo.save(friendship);
        notificationServise.notify("New Friendship Req.");
        return true;
    }


    @Override
    public boolean cancelFriendshipRequestByUserId(Long senderId, Long recipientId) {
        Optional<Friendship> friendshipOptional = getFriendshipByUserIdOrderInsensitive(senderId, recipientId);
        Friendship friendship = friendshipOptional.orElseGet(Friendship::new);
        if (friendshipOptional.isPresent() && friendship.getStatus() == FriendshipStatus.REQUESTED) {
            friendship.setStatus(FriendshipStatus.CANCELLED);
            friendshipRepo.save(friendship);
            notificationServise.notify("Cancel Friendship Req.");
            return true;
        }
        return false;
    }

    @Override
    public boolean acceptFriendshipRequestByUserId(Long senderId, Long recipientId) {
        Optional<Friendship> friendshipOptional = getFriendshipByUserIdOrderInsensitive(senderId, recipientId);
        Friendship friendship = friendshipOptional.orElseGet(Friendship::new);
        if(friendshipOptional.isPresent() && friendship.getStatus() == FriendshipStatus.REQUESTED) {
            friendship.setStatus(FriendshipStatus.ACCEPTED);
            friendshipRepo.save(friendship);
            notificationServise.notify("Accepted Friendship Req.");
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
            notificationServise.notify("Declined Friendship Req.");
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
        if ( friendshipOptional.isPresent() && friendshipOptional.get().getStatus() == FriendshipStatus.ACCEPTED) {
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
            notificationServise.notify("Blocked Friendship Req.");
            return true;
        }
        return false;
    }

    @Override
    public List<FriendshipRequestDto> getAllFriendshipRequestsForUserById(Long userId) {
        return friendshipRepo.getFriendshipRequestsByUserId(userId).stream()
                .map(friendship -> modelMapper.map(friendship, FriendshipRequestDto.class))
                .toList();
    }

    private Optional<Friendship> getFriendshipByUserIdOrderInsensitive(Long userId, Long friendId) {
        return friendshipRepo.findFriendshipByEitherUserId(userId, friendId);
    }

    private List<FriendCardDto> getAllFriendsOfUserById(Long id) {
        return friendshipRepo.getAllFriendshipsByUserId(id).stream()
                .map(friendship -> modelMapper.map(friendship, FriendCardDto.class))
                .toList();
    }
}
