package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.service.ParticipationService;
import greencity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/participation")
@RequiredArgsConstructor
public class ParticipationController {
    private final ParticipationService participationService;
    private final UserService userService;

    @DeleteMapping("/{eventId}")
    public ResponseEntity<String> cancelParticipation(@CurrentUser Principal currentUser, @PathVariable Long eventId) {
        participationService.removeParticipation(userService.findIdByEmail(currentUser.getName()), eventId);
        return ResponseEntity.ok("Participation successfully deleted");
    }
}
