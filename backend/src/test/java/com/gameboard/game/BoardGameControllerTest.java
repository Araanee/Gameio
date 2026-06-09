package com.gameboard.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameboard.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardGameController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class BoardGameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BoardGameService service;

    private BoardGame sampleGame() {
        BoardGame game = new BoardGame();
        game.setId(1L);
        game.setTitle("Catan");
        game.setStatus(BoardGame.Status.APPROVED);
        return game;
    }

    @Test
    void getAll_publicAccess_returns200() throws Exception {
        when(service.getApprovedGames(any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(sampleGame()), PageRequest.of(0, 12), 1));

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Catan"));
    }

    @Test
    void getById_publicAccess_returns200() throws Exception {
        when(service.getById(1L)).thenReturn(sampleGame());

        mockMvc.perform(get("/api/games/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Catan"));
    }

    @Test
    @WithMockUser(roles = "EDITOR")
    void create_asEditor_returns200() throws Exception {
        BoardGame game = sampleGame();
        when(service.create(any())).thenReturn(game);

        mockMvc.perform(post("/api/games")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(game)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Catan"));
    }

    @Test
    void create_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleGame())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "WEBMASTER")
    void updateStatus_asWebmaster_returns200() throws Exception {
        BoardGame game = sampleGame();
        game.setStatus(BoardGame.Status.APPROVED);
        when(service.updateStatus(1L, BoardGame.Status.APPROVED)).thenReturn(game);

        mockMvc.perform(patch("/api/games/1/status")
                        .with(csrf())
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @WithMockUser(roles = "EDITOR")
    void updateStatus_asEditor_returns403() throws Exception {
        mockMvc.perform(patch("/api/games/1/status")
                        .with(csrf())
                        .param("status", "APPROVED"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "WEBMASTER")
    void delete_asWebmaster_returns204() throws Exception {
        mockMvc.perform(delete("/api/games/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "WEBMASTER")
    void getPending_asWebmaster_returns200() throws Exception {
        when(service.getPendingGames()).thenReturn(List.of(sampleGame()));

        mockMvc.perform(get("/api/games/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Catan"));
    }
}
