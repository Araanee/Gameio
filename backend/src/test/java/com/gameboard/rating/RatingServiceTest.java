package com.gameboard.rating;

import com.gameboard.game.BoardGame;
import com.gameboard.game.BoardGameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private BoardGameRepository boardGameRepository;

    @InjectMocks
    private RatingService service;

    private BoardGame approvedGame() {
        BoardGame game = new BoardGame();
        game.setId(1L);
        game.setTitle("Catan");
        game.setStatus(BoardGame.Status.APPROVED);
        return game;
    }

    @Test
    void addRating_validGame_savesRating() {
        BoardGame game = approvedGame();
        when(boardGameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(ratingRepository.findByBoardGameIdAndUserId(1L, "user1")).thenReturn(Optional.empty());
        when(ratingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(ratingRepository.calculateAverageByGameId(1L)).thenReturn(Optional.of(4.0));
        when(boardGameRepository.save(any())).thenReturn(game);

        Rating rating = new Rating();
        rating.setScore(4);
        rating.setUserId("user1");

        Rating result = service.addRating(1L, rating);

        assertThat(result.getScore()).isEqualTo(4);
        assertThat(result.getBoardGame()).isEqualTo(game);
    }

    @Test
    void addRating_pendingGame_throwsException() {
        BoardGame game = new BoardGame();
        game.setId(1L);
        game.setStatus(BoardGame.Status.PENDING);
        when(boardGameRepository.findById(1L)).thenReturn(Optional.of(game));

        Rating rating = new Rating();
        rating.setScore(3);

        assertThatThrownBy(() -> service.addRating(1L, rating))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("approuvé");
    }

    @Test
    void addRating_duplicateUser_throwsException() {
        BoardGame game = approvedGame();
        when(boardGameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(ratingRepository.findByBoardGameIdAndUserId(1L, "user1"))
                .thenReturn(Optional.of(new Rating()));

        Rating rating = new Rating();
        rating.setScore(5);
        rating.setUserId("user1");

        assertThatThrownBy(() -> service.addRating(1L, rating))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("déjà noté");
    }

    @Test
    void addRating_updatesAverageRating() {
        BoardGame game = approvedGame();
        when(boardGameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(ratingRepository.findByBoardGameIdAndUserId(any(), any())).thenReturn(Optional.empty());
        when(ratingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(ratingRepository.calculateAverageByGameId(1L)).thenReturn(Optional.of(3.5));
        when(boardGameRepository.save(any())).thenReturn(game);

        Rating rating = new Rating();
        rating.setScore(3);
        rating.setUserId("user2");
        service.addRating(1L, rating);

        verify(boardGameRepository).save(argThat(g -> g.getAverageRating() == 3.5));
    }

    @Test
    void getRatingsForGame_returnsList() {
        when(ratingRepository.findByBoardGameId(1L)).thenReturn(List.of(new Rating(), new Rating()));

        List<Rating> result = service.getRatingsForGame(1L);

        assertThat(result).hasSize(2);
    }
}
