package be.technofutur.moonname.models;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InscriptionForm {
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9._-]{3,30}", message = "Use 3–30 letters, numbers, dots or dashes")
    private String username;
    @NotBlank
    @Size(min = 8, max = 64, message = "Password must contain 8–64 characters")
    private String password;
}