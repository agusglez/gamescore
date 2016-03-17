package com.king.gamescore.handler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.king.gamescore.score.ScoreRepository;
import com.sun.net.httpserver.HttpExchange;

/**
 * Handler to manage high score level requests
 */
public class HighScoreListHandler implements GameScoreHandler{

	private static final Logger logger = LogManager.getLogger(HighScoreListHandler.class.getName());

    private static final int MAX_HIGH_SCORES = 15;
    private int levelId;

    public HighScoreListHandler(int levelId){
        this.levelId = levelId;
    }

    public void handle(HttpExchange httpExchange) throws IOException{
    	
        logger.info("HighScoreList begin call. Level: " + this.levelId);
        
        String response = ScoreRepository.getInstance().getHighScoreList(levelId, MAX_HIGH_SCORES);
        
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
        //httpExchange.getResponseBody().write(response.getBytes());
        httpExchange.getResponseBody().write(response.getBytes("US-ASCII"));


        logger.info("HighScoreList end call. Level: " + this.levelId + ". Result: " + response);
    }
}
