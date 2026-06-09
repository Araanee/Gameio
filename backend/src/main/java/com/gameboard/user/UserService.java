package com.gameboard.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + username));
        return new User(user.getUsername(), user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
    }

    public List<AppUser> findAll() {
        return repository.findAll();
    }

    public AppUser create(AppUser user) {
        if (repository.existsByUsername(user.getUsername()))
            throw new IllegalArgumentException("Nom d'utilisateur déjà pris.");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    public AppUser update(Long id, AppUser updated) {
        AppUser user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable."));
        user.setRole(updated.getRole());
        if (updated.getPassword() != null && !updated.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(updated.getPassword()));
        return repository.save(user);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public void initDefaultUsers() {
        if (repository.count() == 0) {
            repository.save(new AppUser("user", passwordEncoder.encode("user"), "USER"));
            repository.save(new AppUser("editor", passwordEncoder.encode("editor"), "EDITOR"));
            repository.save(new AppUser("admin", passwordEncoder.encode("admin"), "WEBMASTER"));
        }
    }
}
