package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.dto.RegisterAdminDto;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admins")
@RestController
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<User> createAdministrator(@RequestBody RegisterAdminDto registerAdminDto) {
        User createdAdmin = userService.createAdministrator(registerAdminDto);

        return ResponseEntity.ok(createdAdmin);
    }
}
