package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.dto.RegisterAdminDto;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    private UserService userService;
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        adminController = new AdminController(userService);
    }

    @Test
    void createAdministrator_shouldReturnOkWithCreatedAdmin() {
        RegisterAdminDto registerAdminDto = new RegisterAdminDto();
        registerAdminDto.setUsername("adminUser");
        registerAdminDto.setEmail("admin@example.com");
        registerAdminDto.setPassword("securePass");

        User mockUser = new User();
        mockUser.setUsername("adminUser");
        mockUser.setEmail("admin@example.com");

        when(userService.createAdministrator(registerAdminDto)).thenReturn(mockUser);

        ResponseEntity<User> response = adminController.createAdministrator(registerAdminDto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockUser, response.getBody());

        verify(userService, times(1)).createAdministrator(registerAdminDto);
    }
}
