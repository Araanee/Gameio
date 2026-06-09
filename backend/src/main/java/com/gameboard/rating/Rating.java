package com.gameboard.rating;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gameboard.game.BoardGame;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_game_id", nullable = false)
    private BoardGame boardGame;

    private String userId;

    @Min(1) @Max(5)
    @Column(nullable = false)
    private Integer score;

    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
