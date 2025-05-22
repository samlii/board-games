package net.listopad.boardgame.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.listopad.boardgame.entity.BoardGame;
import net.listopad.boardgame.repository.BoardGameRepository;

@ExtendWith(MockitoExtension.class)
class BoardGameServiceTest {

    @Mock
    private BoardGameRepository boardGameRepository;

    @InjectMocks
    private BoardGameService boardGameService;

    private BoardGame testBoardGame;
    private BoardGame createDto;
    private BoardGame updateDto;

    @BeforeEach
    void setUp() {
        testBoardGame = new BoardGame();
        testBoardGame.setId(1L);
        testBoardGame.setName("Robo Rally");
        testBoardGame.setDescription("A race board game for 2-8 players");
        testBoardGame.setMinPlayers(3);
        testBoardGame.setMaxPlayers(4);
        testBoardGame.setPlayTimeMinutes(90);
        testBoardGame.setCreatedAt(LocalDateTime.now());
        testBoardGame.setUpdatedAt(LocalDateTime.now());

        createDto = new BoardGame(
            "New Board Game",
            "A new exciting board game",
            2,
            6,
            120
        );

        updateDto = new BoardGame();
        updateDto.setName("Updated Game Name");
        updateDto.setDescription("Updated description");
        updateDto.setMinPlayers(2);
        updateDto.setMaxPlayers(8);
        updateDto.setPlayTimeMinutes(150);
    }

    @Test
    void getAllBoardGames_ShouldReturnAllGames() {
        // Given
        BoardGame game2 = new BoardGame();
        game2.setId(2L);
        game2.setName("Monopoly");
        List<BoardGame> expectedGames = Arrays.asList(testBoardGame, game2);
        when(boardGameRepository.findAll()).thenReturn(expectedGames);

        // When
        List<BoardGame> actualGames = boardGameService.getAllBoardGames();

        // Then
        assertThat(actualGames).hasSize(2);
        assertThat(actualGames).containsExactlyElementsOf(expectedGames);
        verify(boardGameRepository).findAll();
    }

    @Test
    void getAllBoardGames_WhenNoGamesExist_ShouldReturnEmptyList() {
        // Given
        when(boardGameRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<BoardGame> actualGames = boardGameService.getAllBoardGames();

        // Then
        assertThat(actualGames).isEmpty();
        verify(boardGameRepository).findAll();
    }

    @Test
    void getBoardGameById_WhenGameExists_ShouldReturnGame() {
        // Given
        when(boardGameRepository.findById(1L)).thenReturn(Optional.of(testBoardGame));

        // When
        Optional<BoardGame> result = boardGameService.getBoardGameById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testBoardGame);
        verify(boardGameRepository).findById(1L);
    }

    @Test
    void getBoardGameById_WhenGameDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(boardGameRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<BoardGame> result = boardGameService.getBoardGameById(1L);

        // Then
        assertThat(result).isEmpty();
        verify(boardGameRepository).findById(1L);
    }

    @Test
    void getBoardGameByName_WhenGameExists_ShouldReturnGame() {
        // Given
        when(boardGameRepository.findByName("Robo Rally")).thenReturn(Optional.of(testBoardGame));

        // When
        Optional<BoardGame> result = boardGameService.getBoardGameByName("Robo Rally");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testBoardGame);
        verify(boardGameRepository).findByName("Robo Rally");
    }

    @Test
    void getBoardGameByName_WhenGameDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(boardGameRepository.findByName("Non-existent Game")).thenReturn(Optional.empty());

        // When
        Optional<BoardGame> result = boardGameService.getBoardGameByName("Non-existent Game");

        // Then
        assertThat(result).isEmpty();
        verify(boardGameRepository).findByName("Non-existent Game");
    }

    @Test
    void searchBoardGames_ShouldReturnMatchingGames() {
        // Given
        List<BoardGame> expectedGames = Arrays.asList(testBoardGame);
        when(boardGameRepository.findByKeyword("Catan")).thenReturn(expectedGames);

        // When
        List<BoardGame> actualGames = boardGameService.searchBoardGames("Catan");

        // Then
        assertThat(actualGames).hasSize(1);
        assertThat(actualGames).containsExactlyElementsOf(expectedGames);
        verify(boardGameRepository).findByKeyword("Catan");
    }

    @Test
    void searchBoardGames_WhenNoMatches_ShouldReturnEmptyList() {
        // Given
        when(boardGameRepository.findByKeyword("NonExistent")).thenReturn(Arrays.asList());

        // When
        List<BoardGame> actualGames = boardGameService.searchBoardGames("NonExistent");

        // Then
        assertThat(actualGames).isEmpty();
        verify(boardGameRepository).findByKeyword("NonExistent");
    }

    @Test
    void createBoardGame_WithValidData_ShouldCreateAndReturnGame() {
        // Given
        when(boardGameRepository.findByName(createDto.getName())).thenReturn(Optional.empty());
        when(boardGameRepository.save(any(BoardGame.class))).thenReturn(testBoardGame);

        // When
        BoardGame result = boardGameService.createBoardGame(createDto);

        // Then
        assertThat(result).isEqualTo(testBoardGame);
        
        ArgumentCaptor<BoardGame> gameCaptor = ArgumentCaptor.forClass(BoardGame.class);
        verify(boardGameRepository).save(gameCaptor.capture());
        
        BoardGame savedGame = gameCaptor.getValue();
        assertThat(savedGame.getName()).isEqualTo(createDto.getName());
        assertThat(savedGame.getDescription()).isEqualTo(createDto.getDescription());
        assertThat(savedGame.getMinPlayers()).isEqualTo(createDto.getMinPlayers());
        assertThat(savedGame.getMaxPlayers()).isEqualTo(createDto.getMaxPlayers());
        assertThat(savedGame.getPlayTimeMinutes()).isEqualTo(createDto.getPlayTimeMinutes());
        
        verify(boardGameRepository).findByName(createDto.getName());
    }

    @Test
    void createBoardGame_WithDuplicateName_ShouldThrowException() {
        // Given
        when(boardGameRepository.findByName(createDto.getName())).thenReturn(Optional.of(testBoardGame));

        // When & Then
        assertThatThrownBy(() -> boardGameService.createBoardGame(createDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Board game with name '" + createDto.getName() + "' already exists");

        verify(boardGameRepository).findByName(createDto.getName());
        verify(boardGameRepository, never()).save(any(BoardGame.class));
    }

    @Test
    void updateBoardGame_WhenGameExists_ShouldUpdateAllFields() {
        // Given
        when(boardGameRepository.findById(1L)).thenReturn(Optional.of(testBoardGame));
        when(boardGameRepository.findByName(updateDto.getName())).thenReturn(Optional.empty());
        when(boardGameRepository.save(any(BoardGame.class))).thenReturn(testBoardGame);

        // When
        Optional<BoardGame> result = boardGameService.updateBoardGame(1L, updateDto);

        // Then
        assertThat(result).isPresent();
        
        ArgumentCaptor<BoardGame> gameCaptor = ArgumentCaptor.forClass(BoardGame.class);
        verify(boardGameRepository).save(gameCaptor.capture());
        
        BoardGame updatedGame = gameCaptor.getValue();
        assertThat(updatedGame.getName()).isEqualTo(updateDto.getName());
        assertThat(updatedGame.getDescription()).isEqualTo(updateDto.getDescription());
        assertThat(updatedGame.getMinPlayers()).isEqualTo(updateDto.getMinPlayers());
        assertThat(updatedGame.getMaxPlayers()).isEqualTo(updateDto.getMaxPlayers());
        assertThat(updatedGame.getPlayTimeMinutes()).isEqualTo(updateDto.getPlayTimeMinutes());
        
        verify(boardGameRepository).findById(1L);
        verify(boardGameRepository).findByName(updateDto.getName());
    }

    @Test
    void updateBoardGame_WhenGameExists_ShouldUpdatePartialFields() {
        // Given
        BoardGame partialUpdateDto = new BoardGame();
        partialUpdateDto.setName("New Name Only");
        // Other fields are null
        
        when(boardGameRepository.findById(1L)).thenReturn(Optional.of(testBoardGame));
        when(boardGameRepository.findByName("New Name Only")).thenReturn(Optional.empty());
        when(boardGameRepository.save(any(BoardGame.class))).thenReturn(testBoardGame);

        // When
        Optional<BoardGame> result = boardGameService.updateBoardGame(1L, partialUpdateDto);

        // Then
        assertThat(result).isPresent();
        
        ArgumentCaptor<BoardGame> gameCaptor = ArgumentCaptor.forClass(BoardGame.class);
        verify(boardGameRepository).save(gameCaptor.capture());
        
        BoardGame updatedGame = gameCaptor.getValue();
        assertThat(updatedGame.getName()).isEqualTo("New Name Only");
        // Original values should be preserved for null fields
        assertThat(updatedGame.getDescription()).isEqualTo(testBoardGame.getDescription());
        assertThat(updatedGame.getMinPlayers()).isEqualTo(testBoardGame.getMinPlayers());
        
        verify(boardGameRepository).findById(1L);
        verify(boardGameRepository).findByName("New Name Only");
    }

    @Test
    void updateBoardGame_WhenGameDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(boardGameRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<BoardGame> result = boardGameService.updateBoardGame(1L, updateDto);

        // Then
        assertThat(result).isEmpty();
        verify(boardGameRepository).findById(1L);
        verify(boardGameRepository, never()).save(any(BoardGame.class));
    }

    @Test
    void updateBoardGame_WithDuplicateName_ShouldThrowException() {
        // Given
        BoardGame anotherGame = new BoardGame();
        anotherGame.setId(2L);
        anotherGame.setName(updateDto.getName());
        
        when(boardGameRepository.findById(1L)).thenReturn(Optional.of(testBoardGame));
        when(boardGameRepository.findByName(updateDto.getName())).thenReturn(Optional.of(anotherGame));

        // When & Then
        assertThatThrownBy(() -> boardGameService.updateBoardGame(1L, updateDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Another board game with name '" + updateDto.getName() + "' already exists");

        verify(boardGameRepository).findById(1L);
        verify(boardGameRepository).findByName(updateDto.getName());
        verify(boardGameRepository, never()).save(any(BoardGame.class));
    }

    @Test
    void updateBoardGame_WithSameGameName_ShouldAllowUpdate() {
        // Given - updating game with its own name
        updateDto.setName(testBoardGame.getName()); // Same name as existing game
        
        when(boardGameRepository.findById(1L)).thenReturn(Optional.of(testBoardGame));
        when(boardGameRepository.findByName(testBoardGame.getName())).thenReturn(Optional.of(testBoardGame));
        when(boardGameRepository.save(any(BoardGame.class))).thenReturn(testBoardGame);

        // When
        Optional<BoardGame> result = boardGameService.updateBoardGame(1L, updateDto);

        // Then
        assertThat(result).isPresent();
        verify(boardGameRepository).findById(1L);
        verify(boardGameRepository).findByName(testBoardGame.getName());
        verify(boardGameRepository).save(any(BoardGame.class));
    }

    @Test
    void deleteBoardGame_WhenGameExists_ShouldDeleteAndReturnTrue() {
        // Given
        when(boardGameRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = boardGameService.deleteBoardGame(1L);

        // Then
        assertThat(result).isTrue();
        verify(boardGameRepository).existsById(1L);
        verify(boardGameRepository).deleteById(1L);
    }

    @Test
    void deleteBoardGame_WhenGameDoesNotExist_ShouldReturnFalse() {
        // Given
        when(boardGameRepository.existsById(1L)).thenReturn(false);

        // When
        boolean result = boardGameService.deleteBoardGame(1L);

        // Then
        assertThat(result).isFalse();
        verify(boardGameRepository).existsById(1L);
        verify(boardGameRepository, never()).deleteById(1L);
    }

    @Test
    void createBoardGame_ShouldCallRepositoryWithCorrectParameters() {
        // Given
        when(boardGameRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(boardGameRepository.save(any(BoardGame.class))).thenReturn(testBoardGame);

        // When
        boardGameService.createBoardGame(createDto);

        // Then
        ArgumentCaptor<BoardGame> gameCaptor = ArgumentCaptor.forClass(BoardGame.class);
        verify(boardGameRepository).save(gameCaptor.capture());
        
        BoardGame capturedGame = gameCaptor.getValue();
        assertThat(capturedGame.getId()).isNull(); // Should be null before saving
        assertThat(capturedGame.getName()).isEqualTo(createDto.getName());
        assertThat(capturedGame.getDescription()).isEqualTo(createDto.getDescription());
        assertThat(capturedGame.getMinPlayers()).isEqualTo(createDto.getMinPlayers());
        assertThat(capturedGame.getMaxPlayers()).isEqualTo(createDto.getMaxPlayers());
        assertThat(capturedGame.getPlayTimeMinutes()).isEqualTo(createDto.getPlayTimeMinutes());
    }

    @Test
    void updateBoardGame_WithNullName_ShouldNotUpdateName() {
        // Given
        BoardGame partialDto = new BoardGame();
        partialDto.setDescription("New Description");
        // name is null
        
        String originalName = testBoardGame.getName();
        when(boardGameRepository.findById(1L)).thenReturn(Optional.of(testBoardGame));
        when(boardGameRepository.save(any(BoardGame.class))).thenReturn(testBoardGame);

        // When
        boardGameService.updateBoardGame(1L, partialDto);

        // Then
        ArgumentCaptor<BoardGame> gameCaptor = ArgumentCaptor.forClass(BoardGame.class);
        verify(boardGameRepository).save(gameCaptor.capture());
        
        BoardGame updatedGame = gameCaptor.getValue();
        assertThat(updatedGame.getName()).isEqualTo(originalName); // Should remain unchanged
        assertThat(updatedGame.getDescription()).isEqualTo("New Description");
        
        verify(boardGameRepository, never()).findByName(anyString());
    }
}