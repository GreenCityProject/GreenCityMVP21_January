package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.friendship.*;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.WrongIdException;
import greencity.service.FriendPageService;
import greencity.service.FriendshipService;
import greencity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/friends")
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final FriendPageService friendPageService;
    private final UserService userService;

    @Autowired
    public FriendshipController(
            FriendshipService friendshipService,
            FriendPageService friendPageService,
            UserService userService) {
        this.friendshipService = friendshipService;
        this.friendPageService = friendPageService;
        this.userService = userService;
    }

    /**
     * Handles the sending of a friendship request from the current user to the specified recipient.
     * This method creates a friendship request for the user represented by {@code userVO}
     * to the user identified by {@code recipientId}. If the request is successfully sent,
     * it returns a response indicating success with a status of {@code 201 Created}.
     * If the request cannot be sent (either because it already exists or for any other reason),
     * it returns a response indicating the failure with a status of {@code 406 Not Acceptable}.
     *
     * @param recipientId the ID of the user to whom the friendship request is being sent.
     *                    Must not be null. A {@link NotNull} validation ensures this.
     * @param userVO     the current user initiating the friendship request.
     *                   This is injected and hidden from the documentation.
     * @return a {@link ResponseEntity} containing {@link FriendshipResponseDto} with the outcome
     *         message and status of the request.
     */
    @Operation(summary = "Request friendship")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "406", description = HttpStatuses.NOT_ACCEPTABLE)
    })
    @PostMapping("/request/{recipientId}/")
    public ResponseEntity<FriendshipResponseDto> requestFriendship(
            @Parameter(hidden = true) @CurrentUser UserVO userVO,
            @PathVariable("recipientId") @NotNull(message = "User ID must not be null.") Long recipientId
            ) {
        boolean isRequested = friendshipService.requestFriendshipByUserId(userVO.getId(), recipientId);

        return (isRequested)
                ? ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new FriendshipResponseDto(true, "Friendship request sent successfully."))
                : ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new FriendshipResponseDto(false, "Friendship request already exists or cannot be sent."));
    }



    @Operation(summary = "Accept friendship request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "406", description = HttpStatuses.NOT_ACCEPTABLE)
    })

    /**
     * Accepts a friendship request from the specified sender for the current user.
     * This method enables the authenticated user (represented by {@code userVO})
     * to accept a friendship request from another user whose ID is specified by {@code senderId}.
     * If the friendship request is accepted successfully, the method returns a response with status
     * {@code 200 OK} along with a success message. If the acceptance fails (for example, if the
     * friendship record does not exist or is not in the REQUESTED state), it returns a response
     * with status {@code 406 Not Acceptable} along with an error message explaining the cause
     * of the failure.
     *
     * @param userVO      the current authenticated user who is accepting the friendship request.
     *                    This parameter is injected and hidden from the API documentation.
     * @param senderId    the ID of the user whose friendship request is being accepted.
     *                    Must not be null, as enforced by the {@link NotNull} validation.
     * @return a {@link ResponseEntity} containing a {@link FriendshipResponseDto}
     *         with the result of the acceptance operation and the appropriate HTTP status.
     */
    @PutMapping("/accept/{senderId}/")
    public ResponseEntity<FriendshipResponseDto> acceptFriendship(
            @Parameter(hidden = true) @CurrentUser UserVO userVO,
            @PathVariable("senderId") @NotNull(message = "User ID must not be null.") Long senderId) {
        boolean isAccepted = friendshipService.acceptFriendshipRequestByUserId(senderId, userVO.getId());
        return (isAccepted)
                ? ResponseEntity.ok(new FriendshipResponseDto(true, "Friendship accepted successfully."))
                : ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new FriendshipResponseDto(false, "Friendship record must exist and be in REQUESTED state"));
    }

    /**
     * Cancels a friendship request previously sent to the specified recipient by the current user.
     * This method allows the authenticated user (represented by {@code userVO})
     * to cancel a friendship request that was sent to another user identified by {@code recipientId}.
     * If the cancellation is successful, it returns a response with status {@code 200 OK}
     * along with a success message. If the cancellation fails (for example, if there was no
     * preceding friendship request to cancel), it returns a response with status
     * {@code 406 Not Acceptable} and an error message indicating that the cancellation has failed.
     *
     * @param userVO      the current authenticated user who is cancelling the friendship request.
     *                    This parameter is injected and hidden from the API documentation.
     * @param recipientId the ID of the user to whom the friendship request was sent and is being cancelled.
     *                    Must not be null, as enforced by the {@link NotNull} validation.
     * @return a {@link ResponseEntity} containing a {@link FriendshipResponseDto}
     *         with the result of the cancellation operation and the appropriate HTTP status.
     */
    @Operation(summary = "Cancel friendship request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "406", description = HttpStatuses.NOT_ACCEPTABLE)
    })
    @PutMapping("/cancel/{recipientId}/")
    public ResponseEntity<FriendshipResponseDto> cancelFriendshipRequest(
            @Parameter(hidden = true) @CurrentUser UserVO userVO,
            @PathVariable("recipientId") @NotNull(message = "User ID must not be null.") Long recipientId) {
        boolean isCancelled  = friendshipService.cancelFriendshipRequestByUserId(userVO.getId(), recipientId);
        return (isCancelled)
                ? ResponseEntity.ok(new FriendshipResponseDto(true, "Friendship request cancelled successfully."))
                : ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new FriendshipResponseDto(false, "Friendship request cancellation has failed."));
    }

    /**
     * Deletes an existing friendship between the current user and a specified friend.
     * This method allows the authenticated user (represented by {@code userVO})
     * to delete a friendship with another user identified by {@code friendId}.
     * If the friendship is successfully deleted, the method returns a response with status
     * {@code 200 OK} along with a success message. If the deletion fails (for example, because
     * the friendship record does not exist or is not in an appropriate state to delete),
     * it returns a response with status {@code 404 Not Found} and an error message indicating
     * that the deletion could not be performed.
     *
     * @param userVO   the current authenticated user who is initiating the deletion of the friendship.
     *                 This parameter is injected and hidden from the API documentation.
     * @param friendId the ID of the friend with whom the friendship is to be deleted.
     *                 Must not be null, as enforced by the {@link NotNull} validation.
     * @return a {@link ResponseEntity} containing a {@link FriendshipResponseDto}
     *         indicating the result of the deletion operation and the appropriate HTTP status.
     */
    @Operation(summary = "Delete friendship record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/delete/{friendId}/")
    public ResponseEntity<FriendshipResponseDto> deleteFriendship(
            @Parameter(hidden = true) @CurrentUser UserVO userVO,
            @PathVariable("friendId") @NotNull(message = "User ID must not be null.") Long friendId) {
        boolean isDeleted = friendshipService.deleteFriendByUserId(userVO.getId(), friendId);
        return  (isDeleted)
                 ? ResponseEntity.ok(new FriendshipResponseDto(true, "Friendship deleted successfully."))
                 : ResponseEntity
                       .status(HttpStatus.NOT_FOUND)
                       .body(new FriendshipResponseDto(false, "Friendship record must exist and be in REQUESTED state"));
    }

    /**
     * Blocks friendship requests from a specified user for the current user.
     * This method allows the authenticated user (represented by {@code userVO})
     * to block all incoming friendship requests from another user identified by {@code senderId}.
     * If the operation is successful, it returns a response with status {@code 200 OK}
     * along with a success message. If the blocking operation fails (such as if the
     * friendship state is not suitable for blocking), the method returns a response
     * with status {@code 406 Not Acceptable} and an error message indicating failure.
     *
     * @param userVO   the current authenticated user who is executing the block operation.
     *                 This parameter is injected and hidden from API documentation.
     * @param senderId the ID of the sender from whom friendship requests are to be blocked.
     *                 Must not be null, as enforced by the {@link NotNull} validation.
     * @return a {@link ResponseEntity} containing a {@link FriendshipResponseDto}
     *         with the outcome of the block operation and the appropriate HTTP status.
     */

    @Operation(summary = "Block friendship requests from User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "406", description = HttpStatuses.NOT_ACCEPTABLE)
    })
    @PutMapping("/block/{senderId}/")
    public ResponseEntity<FriendshipResponseDto> blockFriendshipRequests(
            @Parameter(hidden = true) @CurrentUser UserVO userVO,
            @PathVariable("senderId") @NotNull(message = "User ID must not be null.") Long senderId) {
        boolean isBlocked  = friendshipService.blockFriendshipRequestsFromUserById(senderId, userVO.getId());
        return (isBlocked)
                ? ResponseEntity.ok(new FriendshipResponseDto(true, "Friendship request blocked successfully."))
                : ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new FriendshipResponseDto(false, "Friendship request blockage was unsuccessful."));
    }

    /**
     * Retrieves a list of mutual friends between the specified users.
     * If either userId or targetUserId does not match the currently authenticated user, a FORBIDDEN response is returned.
     *
     * @param userId the ID of the first user
     * @param targetUserId the ID of the second user
     * @return a ResponseEntity containing a list of FriendCardDto representing
     * mutual friends, if found.
     */

    @Operation(summary = "View a list of mutual friends")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/{userId}/mutual/{targetUserId}/")
    public ResponseEntity<List<FriendCardDto>> getMutualFriends(
            @Parameter(hidden = true) @CurrentUser UserVO userVO,
            @PathVariable("userId") @NotNull(message = "User ID must not be null.") Long userId,
            @PathVariable("targetUserId") @NotNull(message = "User ID must not be null.") Long targetUserId) {
        if(isNotCurrentUser(userId) && isNotCurrentUser(targetUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return  ResponseEntity.ok(friendshipService.getAllMutualFriendsByUserId(userId, targetUserId));
    }

    /**
     * Retrieves a list of friendship requests for a specified user.
     * The list will contain instances of the Friendship entity, representing the users who have requested to befriend
     * the specified user.
     *
     * @param userId the ID of the user for whom to retrieve friendship requests
     * @return a ResponseEntity containing a list of RequestedFriendshipDto objects
     * representing all pending friendship requests for the specified user.
     */

    @Operation(summary = "View a list friendship requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/{userId}/requested/")
    public ResponseEntity<List<RequestedFriendshipDto>> getFriendshipRequestsForUserById(
            @Parameter(hidden = true) @CurrentUser UserVO userVO,
            @PathVariable("userId") @NotNull(message = "User ID must not be null.") Long userId) {
        if(isNotCurrentUser(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return  ResponseEntity.ok(friendshipService.getAllFriendshipRequestsForUserById(userId));
    }

    @Operation(summary = "Get a FriendPageDto for a target user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{userId}/friendPage/{targetUserId}/")
    public ResponseEntity<FriendPageDto> getFriendPage(
            @Parameter(hidden = true) @CurrentUser UserVO userVO,
            @PathVariable("userId") @NotNull(message = "User ID must not be null.") Long userId,
            @PathVariable("targetUserId") @NotNull(message = "User ID must not be null.") Long targetUserId
    ) {
        if(isNotCurrentUser(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<FriendshipVO> friendship = friendshipService.findFriendshipByParticipantsId(userId, targetUserId);
        try {
            return ResponseEntity.ok(friendPageService.assembleFriendPage(targetUserId, friendship.map(FriendshipVO::getId)));
        } catch (WrongIdException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Get a FriendPageDto for a target user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{userId}/recommendedFriends/")
    public ResponseEntity<PageableDto<FriendCardDto>> getRecommendedFriends(
            @Parameter(hidden = true) @CurrentUser UserVO userVO,
            @PathVariable("userId") @NotNull(message = "User ID must not be null.") Long userId,
            Pageable pageable) {
        if(isNotCurrentUser(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return  ResponseEntity.ok(friendshipService.recommendFriendsForUser(pageable, userId));
    }

    @GetMapping
    public ResponseEntity<PageableDto<FriendCardDto>> getFriendsOfUser(
            @Parameter(hidden = true) @CurrentUser UserVO userVO,
            @RequestBody(required = false) FriendshipsFilterRequestDto friendshipsFilterRequest) {
        return  ResponseEntity.ok(
                    friendshipService.getAllFriendsByUserId(
                        userVO.getId(),
                        friendshipsFilterRequest)
        );
    }


    // TODO swagger documentation
    protected boolean isNotCurrentUser(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ! (Objects.nonNull(authentication)
                && authentication.isAuthenticated()
                && Objects.nonNull(userService.findIdByEmail(authentication.getPrincipal().toString()))
                && userService.findIdByEmail(authentication.getPrincipal().toString()).equals(id));
    }
}
