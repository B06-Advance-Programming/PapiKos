package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock private UserService userService;

    @InjectMocks private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void allUsers_returnsListOfUsers() {
        List<User> users = List.of(new User(), new User());
        when(userService.allUsers()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.allUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(userService).allUsers();
    }

    @Test
    void authenticatedUser_shouldReturnOk_whenPrincipalIsUser() {
        User mockUser = new User();
        mockUser.setUsername("testuser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ResponseEntity<User> response = userController.authenticatedUser();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockUser, response.getBody());
    }

    @Test
    void authenticatedUser_shouldReturnInternalServerError_whenPrincipalIsNotUser() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("notAUserObject");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ResponseEntity<User> response = userController.authenticatedUser();

        assertEquals(500, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}
