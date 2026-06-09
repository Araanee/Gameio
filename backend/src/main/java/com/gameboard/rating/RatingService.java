package com.gameboard.rating;

import com.gameboard.game.BoardGame;
import com.gameboard.game.BoardGameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final BoardGameRepository boardGameRepository;

    public List<Rating> getRatingsForGame(Long gameId) {
        return ratingRepository.findByBoardGameId(gameId);
    }

    @Transactional
    public Rating updateRating(Long gameId, Long ratingId, Rating updated, String username) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Note introuvable"));
        if (rating.getUserId() == null || !username.equals(rating.getUserId()))
            throw new IllegalStateException("Vous ne pouvez modifier que votre propre note");
        rating.setScore(updated.getScore());
        rating.setComment(updated.getComment());
        Rating saved = ratingRepository.save(rating);
        recalculateAverage(gameId);
        return saved;
    }

    @Transactional
    public void deleteRating(Long gameId, Long ratingId, String username) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Note introuvable"));
        if (rating.getUserId() == null || !username.equals(rating.getUserId()))
            throw new IllegalStateException("Vous ne pouvez supprimer que votre propre note");
        ratingRepository.deleteById(ratingId);
        recalculateAverage(gameId);
    }

    private void recalculateAverage(Long gameId) {
        boardGameRepository.findById(gameId).ifPresent(game -> {
            game.setAverageRating(ratingRepository.calculateAverageByGameId(gameId)
                    .map(avg -> Math.round(avg * 10.0) / 10.0)
                    .orElse(null));
            boardGameRepository.save(game);
        });
    }

    @Transactional
    public Rating addRating(Long gameId, Rating rating) {
        BoardGame game = boardGameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Jeu introuvable : " + gameId));

        if (game.getStatus() != BoardGame.Status.APPROVED) {
            throw new IllegalStateException("Impossible de noter un jeu non approuvé");
        }

        if (rating.getUserId() != null) {
            ratingRepository.findByBoardGameIdAndUserId(gameId, rating.getUserId())
                    .ifPresent(e -> { throw new IllegalStateException("Vous avez déjà noté ce jeu"); });
        }

        rating.setBoardGame(game);
        Rating saved = ratingRepository.save(rating);
        recalculateAverage(gameId);
        return saved;
    }
}
