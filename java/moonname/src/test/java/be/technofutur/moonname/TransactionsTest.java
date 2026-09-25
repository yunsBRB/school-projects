package be.technofutur.moonname;

import be.technofutur.moonname.models.*;
import be.technofutur.moonname.enumss.*;
import be.technofutur.moonname.repositories.*;
import be.technofutur.moonname.services.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:transactions;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa", "spring.datasource.password=test",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class TransactionsTest {
    @Autowired UserRepository utilisateurs;
    @Autowired FuseeRepositories fusees;
    @Autowired MissionRepository missions;
    @Autowired CommandeRepository commandes;
    @Autowired PierreService panier;
    @Autowired CommandeService achats;
    @Autowired TransactionTemplate transaction;

    @Test
    void uneSeuleDernierePlacePourDeuxClients() throws Exception {
        Long[] ids = preparer();
        ajouter(ids[0], ids[2]);
        ajouter(ids[1], ids[2]);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch depart = new CountDownLatch(1);
        try {
            Future<Boolean> a = executor.submit(() -> acheter(ids[0], depart));
            Future<Boolean> b = executor.submit(() -> acheter(ids[1], depart));
            depart.countDown();
            assertThat(a.get(15, TimeUnit.SECONDS) ^ b.get(15, TimeUnit.SECONDS)).isTrue();
            assertThat(missions.findById(ids[2]).orElseThrow().getPlacesDisponibles()).isZero();
            assertThat(commandes.findByUtilisateurIdOrderByIdDesc(ids[0]).size()
                    + commandes.findByUtilisateurIdOrderByIdDesc(ids[1]).size()).isEqualTo(1);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void achatIncompletAnnuleToutesLesModifications() {
        Long[] ids = preparer();
        ajouter(ids[0], ids[2]);
        ajouter(ids[0], ids[3]);
        transaction.executeWithoutResult(s -> missions.findById(ids[3]).orElseThrow().setPlacesDisponibles(0));
        assertThatThrownBy(() -> achats.acheter(ids[0])).isInstanceOf(IllegalArgumentException.class);
        assertThat(missions.findById(ids[2]).orElseThrow().getPlacesDisponibles()).isEqualTo(1);
        assertThat(panier.panier(ids[0])).hasSize(2);
        assertThat(commandes.findByUtilisateurIdOrderByIdDesc(ids[0])).isEmpty();
    }

    private boolean acheter(Long utilisateur, CountDownLatch depart) throws InterruptedException {
        depart.await();
        try {
            achats.acheter(utilisateur);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void ajouter(Long utilisateur, Long mission) {
        PierreForm form = new PierreForm();
        form.setNomInscrit("Demo");
        form.setMessage("");
        form.setMissionId(mission);
        Long pierre = panier.enregistrer(null, form, utilisateur);
        panier.changerPanier(pierre, utilisateur, true);
    }

    private Long[] preparer() {
        return transaction.execute(s -> {
            User a = compte(Role.CLIENT);
            User b = compte(Role.CLIENT);
            User nora = compte(Role.ASTRONAUTE);
            Fusee f = new Fusee();
            f.setNom("Saturn");
            fusees.save(f);
            Misssion m1 = mission(f, nora);
            Misssion m2 = mission(f, nora);
            return new Long[]{a.getId(), b.getId(), m1.getId(), m2.getId()};
        });
    }

    private User compte(Role role) {
        User u = new User();
        u.setUsername(UUID.randomUUID().toString().substring(0, 20));
        u.setPassword("test");
        u.setRole(role);
        return utilisateurs.save(u);
    }

    private Misssion mission(Fusee f, User u) {
        Misssion m = new Misssion();
        m.setNom("Séléné");
        m.setFusee(f);
        m.setAstronaute(u);
        m.setDateDepart(LocalDate.now().plusDays(30));
        m.setPrix(new BigDecimal("29.00"));
        m.setPlacesDisponibles(1);
        m.setStatut(StatutMission.PLANIFIEE);
        return missions.save(m);
    }
}
