package com.gameboard.game;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "board_games")
public class BoardGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;
    private String genre;
    private Integer minPlayers;
    private Integer maxPlayers;

    @Column(name = "release_year")
    private Integer year;

    private Double averageRating;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private String proposedBy;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }
}
