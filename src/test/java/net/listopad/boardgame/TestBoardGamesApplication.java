package net.listopad.boardgame;

import org.springframework.boot.SpringApplication;

import net.listopad.boardgame.BoardGamesApplication;

public class TestBoardGamesApplication {

	public static void main(String[] args) {
		SpringApplication.from(BoardGamesApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
