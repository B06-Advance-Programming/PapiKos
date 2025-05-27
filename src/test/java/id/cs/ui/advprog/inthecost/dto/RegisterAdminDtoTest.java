package id.cs.ui.advprog.inthecost.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterAdminDtoTest {

    @Test
    void testGettersAndSetters() {
        RegisterAdminDto dto = new RegisterAdminDto();

        String email = "admin@example.com";
        String password = "securePassword123";
        String username = "adminUser";

        dto.setEmail(email);
        dto.setPassword(password);
        dto.setUsername(username);

        assertAll(
                () -> assertEquals(email, dto.getEmail(), "Email should match"),
                () -> assertEquals(password, dto.getPassword(), "Password should match"),
                () -> assertEquals(username, dto.getUsername(), "Username should match")
        );
    }
}
