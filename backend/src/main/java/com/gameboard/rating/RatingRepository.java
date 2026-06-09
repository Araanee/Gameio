package com.gameboard.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByBoardGameId(Long boardGameId);

    void deleteByBoardGameId(Long boardGameId);

    Optional<Rating> findByBoardGameIdAndUserId(Long boardGameId, String userId);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.boardGame.id = :gameId")
    Optional<Double> calculateAverageByGameId(Long gameId);
}
