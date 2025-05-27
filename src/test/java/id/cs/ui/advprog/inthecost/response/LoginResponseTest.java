package id.cs.ui.advprog.inthecost.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {

    @Test
    void testTokenGetterAndSetter() {
        LoginResponse response = new LoginResponse();
        String expectedToken = "sample.jwt.token";

        response.setToken(expectedToken);

        assertEquals(expectedToken, response.getToken(), "Token should match the set value");
    }

    @Test
    void testExpiresInGetterAndSetter() {
        LoginResponse response = new LoginResponse();
        long expectedExpiresIn = 3600L;

        response.setExpiresIn(expectedExpiresIn);

        assertEquals(expectedExpiresIn, response.getExpiresIn(), "ExpiresIn should match the set value");
    }

    @Test
    void testFullObjectSetAndGet() {
        LoginResponse response = new LoginResponse();
        String expectedToken = "another.jwt.token";
        long expectedExpiresIn = 7200L;

        response.setToken(expectedToken);
        response.setExpiresIn(expectedExpiresIn);

        assertAll(
                () -> assertEquals(expectedToken, response.getToken(), "Token should match"),
                () -> assertEquals(expectedExpiresIn, response.getExpiresIn(), "ExpiresIn should match")
        );
    }
}
