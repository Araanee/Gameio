package com.gameboard.game;

import com.gameboard.bgg.BggClient;
import com.gameboard.bgg.BggGameDto;
import com.gameboard.kafka.GameEventProducer;
import com.gameboard.rating.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardGameService {

    private final BoardGameRepository repository;
    private final RatingRepository ratingRepository;
    private final BggClient bggClient;
    private final Optional<GameEventProducer> kafkaProducer;

    public Page<BoardGame> getApprovedGames(String title, String genre, Integer year, Integer players, Pageable pageable) {
        String yearStr = year != null ? year.toString() : null;
        return repository.search(title, genre, yearStr, players, pageable);
    }

    public List<BoardGame> getMyProposals(String username) {
        return repository.findByProposedBy(username);
    }

    public List<BoardGame> getPendingGames() {
        return repository.findByStatus(BoardGame.Status.PENDING);
    }

    public BoardGame getById(Long id) {
        if (id == null) throw new RuntimeException("L'identifiant ne peut pas être null");
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jeu introuvable : " + id));
    }

    public List<BoardGame> searchByTitle(String title) {
        return repository.findByTitleContainingIgnoreCase(title);
    }

    public BoardGame create(BoardGame game) {
        game.setStatus(BoardGame.Status.PENDING);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        game.setProposedBy(username);
        enrichFromBgg(game);
        BoardGame saved = repository.save(game);
        Long savedId = saved.getId();
        if (savedId != null) {
            kafkaProducer.ifPresent(p -> p.sendGameImported(savedId, saved.getTitle(), "editor"));
        }
        return saved;
    }

    public Optional<BggGameDto> searchBgg(String title) {
        return bggClient.searchBggId(title).flatMap(bggClient::fetchByBggId);
    }

    public BoardGame updateStatus(Long id, BoardGame.Status status) {
        BoardGame game = getById(id);
        game.setStatus(status);
        return repository.save(game);
    }

    public BoardGame update(Long id, BoardGame updated) {
        BoardGame game = getById(id);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isWebmaster = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_WEBMASTER"));
        if (!isWebmaster) {
            if (game.getStatus() != BoardGame.Status.PENDING)
                throw new IllegalStateException("Vous ne pouvez modifier qu'un jeu en attente.");
            if (!username.equals(game.getProposedBy()))
                throw new IllegalStateException("Vous ne pouvez modifier que vos propres propositions.");
        }
        game.setTitle(updated.getTitle());
        game.setDescription(updated.getDescription());
        game.setImageUrl(updated.getImageUrl());
        game.setGenre(updated.getGenre());
        game.setMinPlayers(updated.getMinPlayers());
        game.setMaxPlayers(updated.getMaxPlayers());
        game.setYear(updated.getYear());
        return repository.save(game);
    }

    @Transactional
    public void delete(Long id) {
        if (id == null) return;
        BoardGame game = getById(id);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isWebmaster = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_WEBMASTER"));
        if (!isWebmaster) {
            if (game.getStatus() != BoardGame.Status.PENDING)
                throw new IllegalStateException("Vous ne pouvez supprimer qu'un jeu en attente.");
            if (!username.equals(game.getProposedBy()))
                throw new IllegalStateException("Vous ne pouvez supprimer que vos propres propositions.");
        }
        ratingRepository.deleteByBoardGameId(id);
        repository.deleteById(id);
    }

    private void enrichFromBgg(BoardGame game) {
        bggClient.searchBggId(game.getTitle()).flatMap(bggClient::fetchByBggId).ifPresent(dto -> {
            if (game.getDescription() == null || game.getDescription().isBlank())
                game.setDescription(dto.getDescription());
            if (game.getImageUrl() == null || game.getImageUrl().isBlank())
                game.setImageUrl(dto.getImageUrl());
            if (game.getMinPlayers() == null) game.setMinPlayers(dto.getMinPlayers());
            if (game.getMaxPlayers() == null) game.setMaxPlayers(dto.getMaxPlayers());
            if (game.getYear() == null) game.setYear(dto.getYear());
        });
    }
}
