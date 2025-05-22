package net.listopad.boardgame.service;

import net.listopad.boardgame.entity.BoardGame;
import net.listopad.boardgame.repository.BoardGameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardGameService {
    
    private final BoardGameRepository boardGameRepository;
    
    public List<BoardGame> getAllBoardGames() {
        return boardGameRepository.findAll();
    }
    
    public Optional<BoardGame> getBoardGameById(Long id) {
        return boardGameRepository.findById(id);
    }
    
    public Optional<BoardGame> getBoardGameByName(String name) {
        return boardGameRepository.findByName(name);
    }
    
    public List<BoardGame> searchBoardGames(String keyword) {
        return boardGameRepository.findByKeyword(keyword);
    }
    
    public BoardGame createBoardGame(BoardGame create) {
        // Check if game with same name already exists
        if (boardGameRepository.findByName(create.getName()).isPresent()) {
            throw new IllegalArgumentException("Board game with name '" + create.getName() + "' already exists");
        }
        
        return boardGameRepository.save(create);
    }
    
    public Optional<BoardGame> updateBoardGame(Long id, BoardGame update) {
        return boardGameRepository.findById(id)
            .map(existingGame -> {
                if (update.getName() != null) {
                    // Check if another game with this name exists
                    Optional<BoardGame> gameWithSameName = boardGameRepository.findByName(update.getName());
                    if (gameWithSameName.isPresent() && !gameWithSameName.get().getId().equals(id)) {
                        throw new IllegalArgumentException("Another board game with name '" + update.getName() + "' already exists");
                    }
                    existingGame.setName(update.getName());
                }
                if (update.getDescription() != null) {
                    existingGame.setDescription(update.getDescription());
                }
                if (update.getMinPlayers() != null) {
                    existingGame.setMinPlayers(update.getMinPlayers());
                }
                if (update.getMaxPlayers() != null) {
                    existingGame.setMaxPlayers(update.getMaxPlayers());
                }
                if (update.getPlayTimeMinutes() != null) {
                    existingGame.setPlayTimeMinutes(update.getPlayTimeMinutes());
                }
                
                return boardGameRepository.save(existingGame);
            });
    }
    
    public boolean deleteBoardGame(Long id) {
        if (boardGameRepository.existsById(id)) {
            boardGameRepository.deleteById(id);
            return true;
        }
        return false;
    }
}