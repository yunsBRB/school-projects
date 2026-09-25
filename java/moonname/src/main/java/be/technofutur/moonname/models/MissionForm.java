package be.technofutur.moonname.models;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissionForm {
    @NotBlank(message = "Name required")
    private String nom;
    @NotNull(message = "Departure date required")
    @Future(message = "Departure must be in the future")
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
    private java.time.LocalDate dateDepart;
    @NotNull(message = "Price required")
    @DecimalMin("0.01")
    @Digits(integer = 8, fraction = 2)
    private java.math.BigDecimal prix;
    @NotNull
    @Min(1)
    private Integer placesDisponibles;
    @NotNull(message = "Select a rocket")
    private Long fuseeId;
    @NotNull(message = "Select an astronaut")
    private Long astronauteId;
}