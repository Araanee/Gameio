package com.gameboard.game;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoardGameRepository extends JpaRepository<BoardGame, Long> {

    List<BoardGame> findByTitleContainingIgnoreCase(String title);

    List<BoardGame> findByStatus(BoardGame.Status status);

    Page<BoardGame> findByStatus(BoardGame.Status status, Pageable pageable);

    @Query(value = "SELECT * FROM board_games WHERE status = 'APPROVED'" +
           " AND (CAST(:title AS TEXT) IS NULL OR title ILIKE CAST(CONCAT('%', :title, '%') AS TEXT))" +
           " AND (CAST(:genre AS TEXT) IS NULL OR genre ILIKE CAST(:genre AS TEXT))" +
           " AND (CAST(:year AS TEXT) IS NULL OR release_year = CAST(:year AS INTEGER))" +
           " AND (:players IS NULL OR (min_players <= :players AND max_players >= :players))",
           countQuery = "SELECT COUNT(*) FROM board_games WHERE status = 'APPROVED'" +
           " AND (CAST(:title AS TEXT) IS NULL OR title ILIKE CAST(CONCAT('%', :title, '%') AS TEXT))" +
           " AND (CAST(:genre AS TEXT) IS NULL OR genre ILIKE CAST(:genre AS TEXT))" +
           " AND (CAST(:year AS TEXT) IS NULL OR release_year = CAST(:year AS INTEGER))" +
           " AND (:players IS NULL OR (min_players <= :players AND max_players >= :players))",
           nativeQuery = true)
    Page<BoardGame> search(@Param("title") String title, @Param("genre") String genre,
                           @Param("year") String year, @Param("players") Integer players,
                           Pageable pageable);

    List<BoardGame> findByProposedBy(String proposedBy);
}
