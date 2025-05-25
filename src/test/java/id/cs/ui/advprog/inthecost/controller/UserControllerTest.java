package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock private UserService userService;
    @Mock private Authentication authentication;

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

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(userService).allUsers();
    }
}
