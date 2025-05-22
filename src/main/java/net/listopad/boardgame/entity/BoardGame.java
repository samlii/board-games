package net.listopad.boardgame.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "board_games")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardGame {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Game name is required")
    @Size(max = 255, message = "Game name must not exceed 255 characters")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Column(nullable = false, length = 2000)
    private String description;
    
    @Column(name = "min_players")
    private Integer minPlayers;
    
    @Column(name = "max_players")
    private Integer maxPlayers;
    
    @Column(name = "play_time_minutes")
    private Integer playTimeMinutes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public BoardGame(String name, String description, Integer minPlayers, Integer maxPlayers, Integer playTimeMinutes) {
        this.name = name;
        this.description = description;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.playTimeMinutes = playTimeMinutes;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
