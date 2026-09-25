package be.technofutur.moonname;

import be.technofutur.moonname.models.*;
import be.technofutur.moonname.enumss.*;
import be.technofutur.moonname.repositories.*;
import be.technofutur.moonname.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:parcours;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@Transactional
class ParcoursTest {
    @Autowired MockMvc mvc;
    @Autowired UserRepository utilisateurs;
    @Autowired FuseeRepositories fusees;
    @Autowired MissionRepository missions;
    @Autowired CommandeRepository commandes;
    @Autowired PierreRepository pierres;
    @Autowired PierreService panier;
    @Autowired CommandeService achats;
    @Autowired MissionService voyages;

    User client, autre, admin, astronaute;
    Misssion mission;
    Fusee fusee;

    @BeforeEach
    void preparer() {
        client = compte("client", Role.CLIENT);
        autre = compte("autre", Role.CLIENT);
        admin = compte("admin", Role.ADMIN);
        astronaute = compte("astronaute", Role.ASTRONAUTE);
        fusee = new Fusee();
        fusee.setNom("Saturn");
        fusee.setPays("Demo");
        fusees.save(fusee);
        mission = new Misssion();
        mission.setNom("Selene");
        mission.setFusee(fusee);
        mission.setAstronaute(astronaute);
        mission.setPrix(new BigDecimal("29.00"));
        mission.setPlacesDisponibles(2);
        mission.setDateDepart(LocalDate.now().plusDays(30));
        mission.setStatut(StatutMission.PLANIFIEE);
        missions.save(mission);
    }

    @Test
    void pagesPubliquesEtFormulaires() throws Exception {
        for (String url : new String[]{"/", "/fusees", "/fusees/" + fusee.getId(),
                "/missions", "/missions/" + mission.getId(), "/login", "/inscription"}) {
            mvc.perform(get(url)).andExpect(status().isOk());
        }
        for (String url : new String[]{"/fusees/ajouter", "/fusees/" + fusee.getId() + "/modifier",
                "/missions/ajouter", "/missions/" + mission.getId() + "/modifier"}) {
            mvc.perform(get(url).with(user(admin))).andExpect(status().isOk());
        }
        mvc.perform(get("/pierres").with(user(client))).andExpect(status().isOk());
        mvc.perform(get("/pierres/ajouter").with(user(client))).andExpect(status().isOk());
        mvc.perform(get("/missions/999999")).andExpect(status().isNotFound());
    }

    @Test
    void achatConserveLePrixEtVideLePanier() throws Exception {
        ajouter();
        Long id = achats.acheter(client.getId());
        assertThat(missions.findById(mission.getId()).orElseThrow().getPlacesDisponibles()).isEqualTo(1);
        assertThat(panier.panier(client.getId())).isEmpty();
        mission.setPrix(new BigDecimal("90.00"));
        assertThat(achats.trouver(id, client.getId()).total()).isEqualByComparingTo("29.00");
        mvc.perform(get("/commandes").with(user(client))).andExpect(status().isOk());
        mvc.perform(get("/commandes/" + id).with(user(client))).andExpect(status().isOk());
    }

    @Test
    void refuseUneDoubleConfirmation() {
        ajouter();
        achats.acheter(client.getId());
        assertThatThrownBy(() -> achats.acheter(client.getId())).isInstanceOf(IllegalArgumentException.class);
        assertThat(commandes.count()).isEqualTo(1);
    }

    @Test
    void refuseUnPanierQuiDepasseLesPlaces() {
        ajouter();
        ajouter();
        mission.setPlacesDisponibles(1);
        missions.saveAndFlush(mission);
        assertThatThrownBy(() -> achats.acheter(client.getId())).isInstanceOf(IllegalArgumentException.class);
        assertThat(commandes.count()).isZero();
        assertThat(panier.panier(client.getId())).hasSize(2);
    }

    @Test
    void controleLesRolesLaProprieteEtLeCsrf() throws Exception {
        mvc.perform(get("/panier")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/fusees/ajouter").with(user(client))).andExpect(status().isForbidden());
        mvc.perform(post("/commandes").with(user(client))).andExpect(status().isForbidden());
        Long pierreId = ajouter();
        assertThatThrownBy(() -> panier.changerPanier(pierreId, autre.getId(), false))
                .isInstanceOf(AccessDeniedException.class);
        Long commandeId = achats.acheter(client.getId());
        mvc.perform(get("/commandes/" + commandeId).with(user(autre))).andExpect(status().isForbidden());
    }

    @Test
    void valideLesFormulairesEtIgnoreLeRoleEnvoye() throws Exception {
        mvc.perform(post("/inscription").with(csrf()).param("username", "nouveau")
                .param("password", "Nouveau123!").param("role", "ADMIN"))
                .andExpect(status().is3xxRedirection());
        assertThat(utilisateurs.findByUsername("nouveau").orElseThrow().getRole()).isEqualTo(Role.CLIENT);
        mvc.perform(post("/pierres/ajouter").with(user(client)).with(csrf())
                .param("nomInscrit", " ").param("message", "Demo").param("missionId", mission.getId().toString()))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("form", "nomInscrit"));
        mvc.perform(post("/fusees/ajouter").with(user(admin)).with(csrf()).param("nom", ""))
                .andExpect(status().isOk()).andExpect(model().attributeHasErrors("form"));
        mvc.perform(post("/missions/ajouter").with(user(admin)).with(csrf()).param("nom", ""))
                .andExpect(status().isOk()).andExpect(model().attributeHasErrors("form"));
    }

    @Test
    void achatParLeControleur() throws Exception {
        mvc.perform(post("/pierres/ajouter").with(user(client)).with(csrf())
                .param("nomInscrit", "Demo").param("message", "Hello Moon")
                .param("missionId", mission.getId().toString())).andExpect(status().is3xxRedirection());
        Long pierreId = pierres.findByProprietaireIdOrderByIdDesc(client.getId()).get(0).getId();
        mvc.perform(post("/panier/ajouter/" + pierreId).with(user(client)).with(csrf()))
                .andExpect(redirectedUrl("/panier"));
        mvc.perform(post("/commandes").with(user(client)).with(csrf())).andExpect(status().is3xxRedirection());
        assertThat(commandes.count()).isEqualTo(1);
        mvc.perform(post("/commandes").with(user(client)).with(csrf())).andExpect(redirectedUrl("/panier"));
    }

    @Test
    void depotReserveALastronauteDeLaMission() throws Exception {
        Long pierreId = ajouter();
        achats.acheter(client.getId());
        mvc.perform(get("/astronaute").with(user(astronaute))).andExpect(status().isOk());
        mvc.perform(get("/astronaute/missions/" + mission.getId()).with(user(astronaute)))
                .andExpect(status().isOk());
        assertThatThrownBy(() -> voyages.deposer(mission.getId(), pierreId, astronaute.getId()))
                .isInstanceOf(IllegalArgumentException.class);
        voyages.avancer(mission.getId(), astronaute.getId());
        assertThatThrownBy(() -> voyages.avancer(mission.getId(), astronaute.getId()))
                .isInstanceOf(IllegalArgumentException.class);
        User second = compte("second", Role.ASTRONAUTE);
        assertThatThrownBy(() -> voyages.deposer(mission.getId(), pierreId, second.getId()))
                .isInstanceOf(AccessDeniedException.class);
        voyages.deposer(mission.getId(), pierreId, astronaute.getId());
        voyages.avancer(mission.getId(), astronaute.getId());
        assertThat(mission.getStatut()).isEqualTo(StatutMission.TERMINEE);
        assertThat(pierres.findById(pierreId).orElseThrow().getStatut()).isEqualTo(StatutPierre.DEPOSEE);
    }

    @Test
    void adminPeutCreerModifierEtSupprimerUneFusee() throws Exception {
        mvc.perform(post("/fusees/ajouter").with(user(admin)).with(csrf()).param("nom", "Aster").param("pays", "Demo"))
                .andExpect(redirectedUrl("/fusees"));
        Fusee f = fusees.findAll().stream().filter(x -> x.getNom().equals("Aster")).findFirst().orElseThrow();
        mvc.perform(post("/fusees/" + f.getId() + "/modifier").with(user(admin)).with(csrf())
                .param("nom", "Aster II").param("pays", "Demo")).andExpect(redirectedUrl("/fusees"));
        assertThat(fusees.findById(f.getId()).orElseThrow().getNom()).isEqualTo("Aster II");
        mvc.perform(post("/fusees/" + f.getId() + "/supprimer").with(user(admin)).with(csrf()))
                .andExpect(redirectedUrl("/fusees"));
        assertThat(fusees.existsById(f.getId())).isFalse();
    }

    private Long ajouter() {
        PierreForm form = new PierreForm();
        form.setNomInscrit("Demo");
        form.setMessage("Hello Moon");
        form.setMissionId(mission.getId());
        Long id = panier.enregistrer(null, form, client.getId());
        panier.changerPanier(id, client.getId(), true);
        return id;
    }

    private User compte(String nom, Role role) {
        User u = new User();
        u.setUsername(nom);
        u.setPassword("test");
        u.setRole(role);
        return utilisateurs.save(u);
    }
}
