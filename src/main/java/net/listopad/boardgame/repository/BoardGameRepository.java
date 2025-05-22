package net.listopad.boardgame.repository;

import net.listopad.boardgame.entity.BoardGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BoardGameRepository extends JpaRepository<BoardGame, Long> {
    
    Optional<BoardGame> findByName(String name);
    
    @Query("SELECT bg FROM BoardGame bg WHERE LOWER(bg.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(bg.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<BoardGame> findByKeyword(@Param("keyword") String keyword);
    
    List<BoardGame> findByMinPlayersLessThanEqualAndMaxPlayersGreaterThanEqual(Integer maxPlayers, Integer minPlayers);
}

