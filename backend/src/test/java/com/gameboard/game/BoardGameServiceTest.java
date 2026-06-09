package com.gameboard.game;

import com.gameboard.bgg.BggClient;
import com.gameboard.rating.RatingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardGameServiceTest {

    @Mock
    private BoardGameRepository repository;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private BggClient bggClient;

    private BoardGameService service;

    @BeforeEach
    void setUp() {
        service = new BoardGameService(repository, ratingRepository, bggClient, Optional.empty());
        var auth = new UsernamePasswordAuthenticationToken(
                "editor", "password", List.of(new SimpleGrantedAuthority("ROLE_EDITOR")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getById_existingId_returnsGame() {
        BoardGame game = new BoardGame();
        game.setTitle("Catan");
        when(repository.findById(1L)).thenReturn(Optional.of(game));

        BoardGame result = service.getById(1L);

        assertThat(result.getTitle()).isEqualTo("Catan");
    }

    @Test
    void getById_unknownId_throwsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getById_nullId_throwsException() {
        assertThatThrownBy(() -> service.getById(null))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void create_setsStatusPending() {
        BoardGame game = new BoardGame();
        game.setTitle("Ticket to Ride");
        when(bggClient.searchBggId(anyString())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> {
            BoardGame g = inv.getArgument(0);
            g.setId(1L);
            return g;
        });

        BoardGame result = service.create(game);

        assertThat(result.getStatus()).isEqualTo(BoardGame.Status.PENDING);
    }

    @Test
    void updateStatus_approvesGame() {
        BoardGame game = new BoardGame();
        game.setTitle("Pandemic");
        game.setStatus(BoardGame.Status.PENDING);
        when(repository.findById(1L)).thenReturn(Optional.of(game));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BoardGame result = service.updateStatus(1L, BoardGame.Status.APPROVED);

        assertThat(result.getStatus()).isEqualTo(BoardGame.Status.APPROVED);
    }

    @Test
    void getApprovedGames_returnsPage() {
        BoardGame game = new BoardGame();
        game.setTitle("Catan");
        game.setStatus(BoardGame.Status.APPROVED);
        Page<BoardGame> page = new PageImpl<>(List.of(game));
        when(repository.search(any(), any(), any(), any(), any())).thenReturn(page);

        Page<BoardGame> result = service.getApprovedGames(null, null, null, null, PageRequest.of(0, 12));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Catan");
    }

    @Test
    void delete_callsRepository() {
        BoardGame game = new BoardGame();
        game.setId(1L);
        game.setStatus(BoardGame.Status.PENDING);
        game.setProposedBy("editor");
        when(repository.findById(1L)).thenReturn(Optional.of(game));

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void getPendingGames_returnsPendingList() {
        BoardGame game = new BoardGame();
        game.setStatus(BoardGame.Status.PENDING);
        when(repository.findByStatus(BoardGame.Status.PENDING)).thenReturn(List.of(game));

        List<BoardGame> result = service.getPendingGames();

        assertThat(result).hasSize(1);
    }
}
