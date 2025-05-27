package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.dto.LoginUserDto;
import id.cs.ui.advprog.inthecost.dto.RegisterUserDto;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.response.LoginResponse;
import id.cs.ui.advprog.inthecost.service.AuthenticationService;
import id.cs.ui.advprog.inthecost.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @Mock private JwtService jwtService;
    @Mock private AuthenticationService authenticationService;

    @InjectMocks private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_shouldReturnRegisteredUser() {
        RegisterUserDto registerDto = new RegisterUserDto();
        registerDto.setEmail("test@example.com");
        registerDto.setPassword("password");
        registerDto.setUsername("testuser");
        registerDto.setRole("PENYEWA");

        User mockUser = new User();
        mockUser.setEmail(registerDto.getEmail());
        mockUser.setUsername(registerDto.getUsername());

        when(authenticationService.signup(registerDto)).thenReturn(mockUser);

        ResponseEntity<User> response = authenticationController.register(registerDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUser, response.getBody());
        verify(authenticationService).signup(registerDto);
    }

    @Test
    void authenticate_shouldReturnLoginResponseWithToken() {
        LoginUserDto loginDto = new LoginUserDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password");

        User mockUser = new User();
        mockUser.setUsername("testuser");

        String fakeToken = "fake-jwt-token";
        long fakeExpire = 3600L;

        when(authenticationService.authenticate(loginDto)).thenReturn(mockUser);
        when(jwtService.generateToken(mockUser)).thenReturn(fakeToken);
        when(jwtService.getExpirationTime()).thenReturn(fakeExpire);

        ResponseEntity<LoginResponse> response = authenticationController.authenticate(loginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fakeToken, response.getBody().getToken());
        assertEquals(fakeExpire, response.getBody().getExpiresIn());

        verify(authenticationService).authenticate(loginDto);
        verify(jwtService).generateToken(mockUser);
        verify(jwtService).getExpirationTime();
    }
}
