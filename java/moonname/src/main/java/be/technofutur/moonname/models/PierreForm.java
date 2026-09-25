package be.technofutur.moonname.models;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PierreForm {
    @NotBlank(message = "Name required")
    @Size(max = 40, message = "40 characters maximum")
    private String nomInscrit;
    @NotBlank(message = "Message required")
    @Size(max = 200, message = "200 characters maximum")
    private String message;
    @NotNull(message = "Select a mission")
    private Long missionId;
    private boolean publier;
}