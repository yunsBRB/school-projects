package be.technofutur.moonname.models;
//Dto = contient les données à transmettre à la page

public record FuseeDto (Long id, String pays, String nom){

    public static FuseeDto fromEntity (Fusee f) {
        return new FuseeDto (
                f.getId(),
                f.getPays(),
                f.getNom()
        );
    }
}