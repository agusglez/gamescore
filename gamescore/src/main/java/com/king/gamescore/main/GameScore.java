package com.king.gamescore.main;

import java.io.IOException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.king.gamescore.server.GameScoreServer;

public class GameScore {

    private static final Logger logger = LogManager.getLogger(GameScore.class.getName());

    public static void main(String[] args){

        try{
            GameScoreServer gameScoreServer = new GameScoreServer();
            gameScoreServer.start();
            System.out.println("\nTo stop the server press Enter");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            scanner.close();
            gameScoreServer.stop();
            System.out.println("Server stopped");
        } catch (IOException ex){
            logger.error("The server has stopped unexpectedly");
        }
    }
}
