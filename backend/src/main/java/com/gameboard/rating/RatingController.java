package com.gameboard.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/games/{gameId}/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService service;

    @GetMapping
    public List<Rating> getRatings(@PathVariable Long gameId) {
        return service.getRatingsForGame(gameId);
    }

    @PostMapping
    public ResponseEntity<Rating> addRating(
            @PathVariable Long gameId,
            @RequestBody Rating rating) {
        return ResponseEntity.ok(service.addRating(gameId, rating));
    }

    @PutMapping("/{ratingId}")
    public ResponseEntity<Rating> updateRating(
            @PathVariable Long gameId,
            @PathVariable Long ratingId,
            @RequestBody Rating rating,
            Principal principal) {
        return ResponseEntity.ok(service.updateRating(gameId, ratingId, rating, principal.getName()));
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable Long gameId,
            @PathVariable Long ratingId,
            Principal principal) {
        service.deleteRating(gameId, ratingId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
