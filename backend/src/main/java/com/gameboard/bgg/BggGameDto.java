package com.gameboard.bgg;

import lombok.Data;

@Data
public class BggGameDto {
    private String title;
    private String description;
    private String imageUrl;
    private Integer minPlayers;
    private Integer maxPlayers;
    private Integer year;
}
