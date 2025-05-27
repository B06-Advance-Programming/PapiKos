package id.cs.ui.advprog.inthecost.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginUserDtoTest {

    @Test
    void testEmailGetterAndSetter() {
        LoginUserDto dto = new LoginUserDto();
        String expectedEmail = "test@example.com";

        dto.setEmail(expectedEmail);

        assertEquals(expectedEmail, dto.getEmail(), "Email should match the set value");
    }

    @Test
    void testPasswordGetterAndSetter() {
        LoginUserDto dto = new LoginUserDto();
        String expectedPassword = "securePassword123";

        dto.setPassword(expectedPassword);

        assertEquals(expectedPassword, dto.getPassword(), "Password should match the set value");
    }

    @Test
    void testFullObjectSetAndGet() {
        LoginUserDto dto = new LoginUserDto();
        String expectedEmail = "user@example.com";
        String expectedPassword = "mySecretPass";

        dto.setEmail(expectedEmail);
        dto.setPassword(expectedPassword);

        assertAll(
                () -> assertEquals(expectedEmail, dto.getEmail(), "Email should match"),
                () -> assertEquals(expectedPassword, dto.getPassword(), "Password should match")
        );
    }
}
