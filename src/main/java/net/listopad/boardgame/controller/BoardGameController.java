package net.listopad.boardgame.controller;

import net.listopad.boardgame.entity.BoardGame;
import net.listopad.boardgame.service.BoardGameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/board-games")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BoardGameController {
    
    private final BoardGameService boardGameService;
    
    @GetMapping
    public ResponseEntity<List<BoardGame>> getAllBoardGames() {
        List<BoardGame> games = boardGameService.getAllBoardGames();
        return ResponseEntity.ok(games);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BoardGame> getBoardGameById(@PathVariable Long id) {
        return boardGameService.getBoardGameById(id)
            .map(game -> ResponseEntity.ok(game))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<BoardGame>> searchBoardGames(@RequestParam String keyword) {
        List<BoardGame> games = boardGameService.searchBoardGames(keyword);
        return ResponseEntity.ok(games);
    }
    
    @PostMapping
    public ResponseEntity<?> createBoardGame(@Valid @RequestBody BoardGame newBoardGame) {
        try {
            BoardGame createdGame = boardGameService.createBoardGame(newBoardGame);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdGame);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBoardGame(@PathVariable Long id, @Valid @RequestBody BoardGame updateBoardGame) {
        try {
            return boardGameService.updateBoardGame(id, updateBoardGame)
                .map(updatedGame -> ResponseEntity.ok(updatedGame))
                .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoardGame(@PathVariable Long id) {
        if (boardGameService.deleteBoardGame(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
