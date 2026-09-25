package be.technofutur.moonname.services;

import be.technofutur.moonname.models.*;
import be.technofutur.moonname.enumss.*;
import be.technofutur.moonname.models.User;
import be.technofutur.moonname.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService implements UserDetailsService {
    private final UserRepository utilisateurs;
    private final PasswordEncoder encoder;

    public UserDetails loadUserByUsername(String username) {
        return utilisateurs.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }

    @Transactional
    public void inscrire(InscriptionForm form) {
        if (utilisateurs.findByUsername(form.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already used");
        }
        if (form.getPassword().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Password exceeds 72 UTF-8 bytes");
        }
        User u = new User();
        u.setUsername(form.getUsername());
        u.setPassword(encoder.encode(form.getPassword()));
        u.setRole(Role.CLIENT);
        utilisateurs.save(u);
    }

    public List<UserDto> astronautes() {
        return utilisateurs.findByRole(Role.ASTRONAUTE).stream().map(UserDto::fromEntity).toList();
    }
}