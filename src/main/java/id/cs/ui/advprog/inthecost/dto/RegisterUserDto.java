package id.cs.ui.advprog.inthecost.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterUserDto {
    private String email;

    private String password;

    private String username;

    private String role;
}
