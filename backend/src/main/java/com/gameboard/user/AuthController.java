package com.gameboard.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> me(Authentication authentication) {
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("USER");
        return ResponseEntity.ok(Map.of("username", authentication.getName(), "role", role));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest req) {
        if (req.username() == null || req.username().isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Le nom d'utilisateur est requis."));
        if (req.password() == null || req.password().length() < 4)
            return ResponseEntity.badRequest().body(Map.of("error", "Le mot de passe doit faire au moins 4 caractères."));
        try {
            userService.create(new AppUser(req.username().trim(), req.password(), "USER"));
            return ResponseEntity.ok(Map.of("message", "Compte créé avec succès."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
