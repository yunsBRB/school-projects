package be.technofutur.moonname.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(r -> r
                        .requestMatchers("/pierres/**", "/panier/**", "/commandes/**").hasAuthority("CLIENT")
                        .requestMatchers("/astronaute/**").hasAuthority("ASTRONAUTE")
                        .requestMatchers("/fusees/ajouter", "/fusees/*/modifier", "/fusees/*/supprimer",
                                "/missions/ajouter", "/missions/*/modifier", "/missions/*/supprimer").hasAuthority("ADMIN")
                        .anyRequest().permitAll())
                .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
                .logout(l -> l.logoutSuccessUrl("/login?logout"))
                .build();
    }
}