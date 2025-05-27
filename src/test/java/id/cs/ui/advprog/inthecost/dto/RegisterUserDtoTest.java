package id.cs.ui.advprog.inthecost.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterUserDtoTest {

    @Test
    void testGettersAndSetters() {
        RegisterUserDto dto = new RegisterUserDto();

        String email = "user@example.com";
        String password = "securePass456";
        String username = "testUser";
        String role = "PENYEWA";

        dto.setEmail(email);
        dto.setPassword(password);
        dto.setUsername(username);
        dto.setRole(role);

        assertAll(
                () -> assertEquals(email, dto.getEmail(), "Email should match"),
                () -> assertEquals(password, dto.getPassword(), "Password should match"),
                () -> assertEquals(username, dto.getUsername(), "Username should match"),
                () -> assertEquals(role, dto.getRole(), "Role should match")
        );
    }
}
