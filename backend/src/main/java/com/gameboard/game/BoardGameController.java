package com.gameboard.game;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class BoardGameController {

    private final BoardGameService service;

    @GetMapping
    public Page<BoardGame> getAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer players,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        String column = switch (sortBy) {
            case "averageRating" -> "average_rating";
            case "year" -> "release_year";
            default -> "title";
        };
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(column).descending()
                : Sort.by(column).ascending();
        return service.getApprovedGames(title, genre, year, players, PageRequest.of(page, size, sort));
    }

    @GetMapping("/my-proposals")
    public List<BoardGame> getMyProposals(java.security.Principal principal) {
        return service.getMyProposals(principal.getName());
    }

    @GetMapping("/pending")
    public List<BoardGame> getPending() {
        return service.getPendingGames();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardGame> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/bgg/search")
    public ResponseEntity<?> searchBgg(@RequestParam String title) {
        return service.searchBgg(title)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BoardGame> create(@RequestBody BoardGame game) {
        return ResponseEntity.ok(service.create(game));
    }

@PatchMapping("/{id}/status")
    public ResponseEntity<BoardGame> updateStatus(
            @PathVariable Long id,
            @RequestParam BoardGame.Status status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardGame> update(
            @PathVariable Long id,
            @RequestBody BoardGame game) {
        return ResponseEntity.ok(service.update(id, game));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
