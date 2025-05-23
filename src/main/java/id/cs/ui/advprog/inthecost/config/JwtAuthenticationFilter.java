package id.cs.ui.advprog.inthecost.config;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import id.cs.ui.advprog.inthecost.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userIdentifier = jwtService.extractUsername(jwt); // Could be username or email
            log.debug("Processing JWT for user: {}", userIdentifier);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userIdentifier != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userIdentifier);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Try to find the user by email first
                    Optional<User> userOpt = userRepository.findByEmail(userIdentifier);

                    // If user not found by email, try to find by username
                    if (userOpt.isEmpty()) {
                        userOpt = userRepository.findByUsername(userIdentifier);
                    }

                    // Get user or throw exception
                    User user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,  // Use the User entity as principal
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authentication successful for user: {}", userIdentifier);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.error("JWT Authentication error: ", exception);
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}