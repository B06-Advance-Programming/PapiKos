package id.cs.ui.advprog.inthecost.controller;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Authentication principal type: {}", authentication.getPrincipal().getClass().getName());

        if (!(authentication.getPrincipal() instanceof User)) {
            log.error("Principal is not a User instance: {}", authentication.getPrincipal());
            return ResponseEntity.internalServerError().build();
        }

        User currentUser = (User) authentication.getPrincipal();
        log.debug("Current user: {}", currentUser.getUsername());
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> allUsers() {
        List<User> users = userService.allUsers();
        log.debug("Retrieved {} users", users.size());
        return ResponseEntity.ok(users);
    }
}