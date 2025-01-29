package greencity.controller;

import greencity.dto.friendship.FriendCardDto;
import greencity.dto.friendship.RequestedFriendshipDto;
import greencity.dto.friendship.FriendshipResponseDto;
import greencity.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
public class FriendshipController {

    FriendshipService friendshipService;

    @Autowired
    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @PostMapping("/{senderId}/request/{recipientId}/")
    public ResponseEntity<FriendshipResponseDto> requestFriendship(
            @PathVariable("senderId") Long senderId,
            @PathVariable("recipientId") Long recipientId) {
        boolean isRequested = friendshipService.requestFriendshipByUserId(senderId, recipientId);

        return (isRequested)
                ? ResponseEntity.ok(new FriendshipResponseDto(true, "Friendship request sent successfully."))
                : ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new FriendshipResponseDto(false, "Friendship request already exists or cannot be sent."));
    }

    @PutMapping("/{senderId}/accept/{recipientId}/")
    public ResponseEntity<FriendshipResponseDto> acceptFriendship(
            @PathVariable("senderId") Long senderId,
            @PathVariable("recipientId") Long recipientId) {
        boolean isAccepted = friendshipService.acceptFriendshipRequestByUserId(senderId, recipientId);
        return (isAccepted)
                ? ResponseEntity.ok(new FriendshipResponseDto(true, "Friendship accepted successfully."))
                : ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new FriendshipResponseDto(false, "Friendship record must exist and be in REQUESTED state"));
    }

    @PutMapping("/{senderId}/cancel/{recipientId}/")
    public ResponseEntity<FriendshipResponseDto> cancelFriendshipRequest(
            @PathVariable("senderId") Long senderId,
            @PathVariable("recipientId") Long recipientId) {
        boolean isCancelled  = friendshipService.cancelFriendshipRequestByUserId(senderId, recipientId);
        return (isCancelled)
                ? ResponseEntity.ok(new FriendshipResponseDto(true, "Friendship request cancelled successfully."))
                : ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new FriendshipResponseDto(false, "Friendship request cancellation has failed."));
    }

    @DeleteMapping("/{userId}/delete/{friendId}/")
    public ResponseEntity<FriendshipResponseDto> deleteFriendship(
            @PathVariable("userId") Long userId,
            @PathVariable("friendId") Long friendId) {
        boolean isDeleted = friendshipService.deleteFriendByUserId(userId, friendId);
        return  (isDeleted)
                 ? ResponseEntity.ok(new FriendshipResponseDto(true, "Friendship deleted successfully."))
                 : ResponseEntity
                       .status(HttpStatus.CONFLICT)
                       .body(new FriendshipResponseDto(false, "Friendship record must exist and be in REQUESTED state"));
    }

    @GetMapping("/{userId}/areFriends/{friendId}/")
    public ResponseEntity<FriendshipResponseDto> checkFriendship(
            @PathVariable("userId") Long userId,
            @PathVariable("friendId") Long friendId) {
        boolean areFriends = friendshipService.areFriends(userId, friendId);
        return  (areFriends)
                 ? ResponseEntity.ok(new FriendshipResponseDto(true, "Friendship record exists and is in ACCEPTED state"))
                 : ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new FriendshipResponseDto(false, "Friendship record must exist and be in ACCEPTED state"));
    }

    @PutMapping("/{senderId}/block/{recipientId}/")
    public ResponseEntity<FriendshipResponseDto> blockFriendshipRequests(
            @PathVariable("senderId") Long senderId,
            @PathVariable("recipientId") Long recipientId) {
        boolean isBlocked  = friendshipService.blockFriendshipRequestsFromUserById(senderId, recipientId);
        return (isBlocked)
                ? ResponseEntity.ok(new FriendshipResponseDto(true, "Friendship request blocked successfully."))
                : ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new FriendshipResponseDto(false, "Friendship request blockage was unsuccessful."));
    }

    @GetMapping("/{userId}/mutual/{targetUserId}/")
    public ResponseEntity<List<FriendCardDto>> getMutualFriends(
            @PathVariable("userId") Long userId,
            @PathVariable("targetUserId") Long targetUserId) {
        return  ResponseEntity.ok(friendshipService.getAllMutualFriendsByUserId(userId, targetUserId));
    }

    @GetMapping("/{userId}/requested/")
    public ResponseEntity<List<RequestedFriendshipDto>> getFriendshipRequests(
            @PathVariable("userId") Long userId) {
        return  ResponseEntity.ok(friendshipService.getAllFriendshipRequestsForUserById(userId));
    }
}
