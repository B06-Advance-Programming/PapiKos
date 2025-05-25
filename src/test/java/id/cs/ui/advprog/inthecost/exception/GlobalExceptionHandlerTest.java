package id.cs.ui.advprog.inthecost.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    public void testHandleBadCredentialsException() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");
        ProblemDetail detail = handler.handleSecurityException(ex);

        assertEquals(401, detail.getStatus());
        assertEquals("Bad credentials", detail.getDetail());
        assertEquals("The username or password is incorrect", detail.getProperties().get("description"));
    }

    @Test
    public void testHandleAccountStatusException() {
        AccountStatusException ex = mock(AccountStatusException.class);
        when(ex.getMessage()).thenReturn("Account locked");
        ProblemDetail detail = handler.handleSecurityException(ex);

        assertEquals(403, detail.getStatus());
        assertEquals("Account locked", detail.getDetail());
        assertEquals("The account is locked", detail.getProperties().get("description"));
    }

    @Test
    public void testHandleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        ProblemDetail detail = handler.handleSecurityException(ex);

        assertEquals(403, detail.getStatus());
        assertEquals("Access denied", detail.getDetail());
        assertEquals("You are not authorized to access this resource", detail.getProperties().get("description"));
    }

    @Test
    public void testHandleSignatureException() {
        SignatureException ex = new SignatureException("Invalid signature");
        ProblemDetail detail = handler.handleSecurityException(ex);

        assertEquals(403, detail.getStatus());
        assertEquals("Invalid signature", detail.getDetail());
        assertEquals("The JWT signature is invalid", detail.getProperties().get("description"));
    }

    @Test
    public void testHandleExpiredJwtException() {
        ExpiredJwtException ex = mock(ExpiredJwtException.class);
        when(ex.getMessage()).thenReturn("Token expired");
        ProblemDetail detail = handler.handleSecurityException(ex);

        assertEquals(403, detail.getStatus());
        assertEquals("Token expired", detail.getDetail());
        assertEquals("The JWT token has expired", detail.getProperties().get("description"));
    }

    @Test
    public void testHandleGenericException() {
        Exception ex = new Exception("General error");
        ProblemDetail detail = handler.handleSecurityException(ex);

        assertEquals(500, detail.getStatus());
        assertEquals("General error", detail.getDetail());
        assertEquals("Unknown internal server error.", detail.getProperties().get("description"));
    }
}
