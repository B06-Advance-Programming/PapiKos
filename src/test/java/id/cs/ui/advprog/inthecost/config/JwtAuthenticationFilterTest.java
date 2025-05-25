package id.cs.ui.advprog.inthecost.config;

import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import id.cs.ui.advprog.inthecost.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock private JwtService jwtService;
    @Mock private UserDetailsService userDetailsService;
    @Mock private HandlerExceptionResolver handlerExceptionResolver;
    @Mock private UserRepository userRepository;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNoAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService, userRepository);
    }

    @Test
    void testValidTokenByEmail() throws Exception {
        String token = "valid.jwt.token";
        String userIdentifier = "user@example.com";
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(userIdentifier)
                .password("password")
                .authorities("ROLE_USER")
                .build();

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(userIdentifier);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(userIdentifier);
        when(userDetailsService.loadUserByUsername(userIdentifier)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);
        when(userRepository.findByEmail(userIdentifier)).thenReturn(Optional.of(user));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testValidTokenByUsername() throws Exception {
        String token = "valid.jwt.token";
        String userIdentifier = "username";
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(userIdentifier)
                .password("password")
                .authorities("ROLE_USER")
                .build();

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(userIdentifier);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(userIdentifier);
        when(userDetailsService.loadUserByUsername(userIdentifier)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);
        when(userRepository.findByEmail(userIdentifier)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(userIdentifier)).thenReturn(Optional.of(user));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
