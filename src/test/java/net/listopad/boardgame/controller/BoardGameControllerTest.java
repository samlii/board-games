package net.listopad.boardgame.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.listopad.boardgame.entity.BoardGame;
import net.listopad.boardgame.service.BoardGameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardGameController.class)
class BoardGameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BoardGameService boardGameService;

    @Autowired
    private ObjectMapper objectMapper;

    private BoardGame testBoardGame;
    private BoardGame createDto;
    private BoardGame updateDto;

    @BeforeEach
    void setUp() {
        testBoardGame = new BoardGame();
        testBoardGame.setId(1L);
        testBoardGame.setName("RoboRally");
        testBoardGame.setDescription("A race board game for 2-8 players");
        testBoardGame.setMinPlayers(3);
        testBoardGame.setMaxPlayers(4);
        testBoardGame.setPlayTimeMinutes(90);
        testBoardGame.setCreatedAt(LocalDateTime.now());
        testBoardGame.setUpdatedAt(LocalDateTime.now());

        createDto = new BoardGame(
            "RoboRally",
            "A race board game for 2-8 players",
            3,
            4,
            90
        );

        updateDto = new BoardGame();
        updateDto.setName("RoboRally - Updated");
        updateDto.setDescription("An updated race board game for 2-8 players");
    }

    @Test
    void getAllBoardGames_ShouldReturnListOfGames() throws Exception {
        // Given
        List<BoardGame> games = Arrays.asList(testBoardGame);
        when(boardGameService.getAllBoardGames()).thenReturn(games);

        // When & Then
        mockMvc.perform(get("/api/board-games"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("RoboRally"))
                .andExpect(jsonPath("$[0].description").value("A race board game for 2-8 players"))
                .andExpect(jsonPath("$[0].minPlayers").value(3))
                .andExpect(jsonPath("$[0].maxPlayers").value(4))
                .andExpect(jsonPath("$[0].playTimeMinutes").value(90));

        verify(boardGameService).getAllBoardGames();
    }

    @Test
    void getBoardGameById_WhenGameExists_ShouldReturnGame() throws Exception {
        // Given
        when(boardGameService.getBoardGameById(1L)).thenReturn(Optional.of(testBoardGame));

        // When & Then
        mockMvc.perform(get("/api/board-games/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("RoboRally"));

        verify(boardGameService).getBoardGameById(1L);
    }

    @Test
    void getBoardGameById_WhenGameDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        when(boardGameService.getBoardGameById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/board-games/1"))
                .andExpect(status().isNotFound());

        verify(boardGameService).getBoardGameById(1L);
    }

    @Test
    void searchBoardGames_ShouldReturnMatchingGames() throws Exception {
        // Given
        List<BoardGame> games = Arrays.asList(testBoardGame);
        when(boardGameService.searchBoardGames("Catan")).thenReturn(games);

        // When & Then
        mockMvc.perform(get("/api/board-games/search")
                .param("keyword", "Catan"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("RoboRally"));

        verify(boardGameService).searchBoardGames("Catan");
    }

    @Test
    void createBoardGame_WithValidData_ShouldReturnCreatedGame() throws Exception {
        // Given
        when(boardGameService.createBoardGame(any(BoardGame.class))).thenReturn(testBoardGame);

        // When & Then
        mockMvc.perform(post("/api/board-games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("RoboRally"));

        verify(boardGameService).createBoardGame(any(BoardGame.class));
    }

    @Test
    void createBoardGame_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        BoardGame invalidDto = new BoardGame();
        invalidDto.setName(""); // Invalid: blank name
        invalidDto.setDescription(""); // Invalid: blank description

        // When & Then
        mockMvc.perform(post("/api/board-games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists());

        verify(boardGameService, never()).createBoardGame(any(BoardGame.class));
    }

    @Test
    void createBoardGame_WithDuplicateName_ShouldReturnBadRequest() throws Exception {
        // Given
        when(boardGameService.createBoardGame(any(BoardGame.class)))
                .thenThrow(new IllegalArgumentException("Board game with name 'RoboRally' already exists"));

        // When & Then
        mockMvc.perform(post("/api/board-games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Board game with name 'RoboRally' already exists"));

        verify(boardGameService).createBoardGame(any(BoardGame.class));
    }

    @Test
    void updateBoardGame_WhenGameExists_ShouldReturnUpdatedGame() throws Exception {
        // Given
        BoardGame updatedGame = new BoardGame();
        updatedGame.setId(1L);
        updatedGame.setName("RoboRally - Updated");
        updatedGame.setDescription("An updated race board game for 2-8 players");
        updatedGame.setMinPlayers(3);
        updatedGame.setMaxPlayers(4);
        updatedGame.setPlayTimeMinutes(90);

        when(boardGameService.updateBoardGame(eq(1L), any(BoardGame.class)))
                .thenReturn(Optional.of(updatedGame));

        // When & Then
        mockMvc.perform(put("/api/board-games/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("RoboRally - Updated"))
                .andExpect(jsonPath("$.description").value("An updated race board game for 2-8 players"));

        verify(boardGameService).updateBoardGame(eq(1L), any(BoardGame.class));
    }

    @Test
    void updateBoardGame_WhenGameDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        when(boardGameService.updateBoardGame(eq(1L), any(BoardGame.class)))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/board-games/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());

        verify(boardGameService).updateBoardGame(eq(1L), any(BoardGame.class));
    }

    @Test
    void updateBoardGame_WithDuplicateName_ShouldReturnBadRequest() throws Exception {
        // Given
        when(boardGameService.updateBoardGame(eq(1L), any(BoardGame.class)))
                .thenThrow(new IllegalArgumentException("Another board game with name 'Duplicate Name' already exists"));

        // When & Then
        mockMvc.perform(put("/api/board-games/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Another board game with name 'Duplicate Name' already exists"));

        verify(boardGameService).updateBoardGame(eq(1L), any(BoardGame.class));
    }

    @Test
    void updateBoardGame_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        BoardGame invalidUpdateDto = new BoardGame();
        invalidUpdateDto.setName("A".repeat(256)); // Invalid: exceeds max length
        invalidUpdateDto.setMinPlayers(0); // Invalid: less than 1

        // When & Then
        mockMvc.perform(put("/api/board-games/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.minPlayers").exists());

        verify(boardGameService, never()).updateBoardGame(eq(1L), any(BoardGame.class));
    }

    @Test
    void deleteBoardGame_WhenGameExists_ShouldReturnNoContent() throws Exception {
        // Given
        when(boardGameService.deleteBoardGame(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/board-games/1"))
                .andExpect(status().isNoContent());

        verify(boardGameService).deleteBoardGame(1L);
    }

    @Test
    void deleteBoardGame_WhenGameDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        when(boardGameService.deleteBoardGame(1L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/board-games/1"))
                .andExpect(status().isNotFound());

        verify(boardGameService).deleteBoardGame(1L);
    }

    @Test
    void createBoardGame_WithNullValues_ShouldReturnBadRequest() throws Exception {
        // Given
        BoardGame nullDto = new BoardGame();
        // All fields are null

        // When & Then
        mockMvc.perform(post("/api/board-games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists());

        verify(boardGameService, never()).createBoardGame(any(BoardGame.class));
    }

    @Test
    void createBoardGame_WithInvalidPlayerCounts_ShouldReturnBadRequest() throws Exception {
        // Given
        BoardGame invalidDto = new BoardGame(
            "Valid Name",
            "Valid Description",
            0, // Invalid: less than 1
            0, // Invalid: less than 1
            0  // Invalid: less than 1
        );

        // When & Then
        mockMvc.perform(post("/api/board-games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.minPlayers").exists())
                .andExpect(jsonPath("$.maxPlayers").exists())
                .andExpect(jsonPath("$.playTimeMinutes").exists());

        verify(boardGameService, never()).createBoardGame(any(BoardGame.class));
    }

    @Test
    void getAllBoardGames_WhenNoGamesExist_ShouldReturnEmptyList() throws Exception {
        // Given
        when(boardGameService.getAllBoardGames()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/board-games"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(boardGameService).getAllBoardGames();
    }

    @Test
    void searchBoardGames_WithEmptyResults_ShouldReturnEmptyList() throws Exception {
        // Given
        when(boardGameService.searchBoardGames("NonExistentGame")).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/board-games/search")
                .param("keyword", "NonExistentGame"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(boardGameService).searchBoardGames("NonExistentGame");
    }
}