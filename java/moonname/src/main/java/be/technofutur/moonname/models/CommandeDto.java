package be.technofutur.moonname.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CommandeDto(Long id, LocalDateTime dateCreation, List<BilletDto> billets) {
    public static CommandeDto fromEntity(Commande c) {
        return new CommandeDto(c.getId(), c.getDateCreation(),
                c.getBillets().stream().map(BilletDto::fromEntity).toList());
    }

    public BigDecimal total() {
        return billets.stream().map(BilletDto::prix).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}