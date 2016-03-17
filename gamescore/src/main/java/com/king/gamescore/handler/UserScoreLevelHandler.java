package com.king.gamescore.handler;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.king.gamescore.score.Score;
import com.king.gamescore.score.ScoreRepository;
import com.king.gamescore.session.SessionManager;
import com.sun.net.httpserver.HttpExchange;

/**
 * Handler to manage post user´s score to a level
 */
public class UserScoreLevelHandler implements GameScoreHandler{

	private static final Logger logger = LogManager.getLogger(UserScoreLevelHandler.class.getName());

	private int levelId;
	private String sessionKey;
	private String score;

	public UserScoreLevelHandler(int levelId, String sessionKey, String score){
		this.levelId = levelId;
		this.sessionKey = sessionKey;
		this.score = score;
	}

	public void handle(HttpExchange httpExchange) throws IOException{

        logger.info("UserScoreLevel begin call. Level: " + this.levelId + ". Score: " + this.score);

		if (!SessionManager.getInstance().sessionIsValid(sessionKey)){
			SessionManager.getInstance().removeSession(sessionKey);
			httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAUTHORIZED, -1);
		}
		else{
			if(scoreIsValid(this.score)){
				String userId = SessionManager.getInstance().getSession(sessionKey).getUserId().toString();
				ScoreRepository.getInstance().putScore(levelId, new Score(Integer.parseInt(score), userId));
				httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, -1);
			} else{
				httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, -1);
			}
		}
		
        logger.info("UserScoreLevel end call. Level: " + this.levelId + ". Score: " + this.score);
	}

	/**
	 * Checks if a score is a valid number
	 * @param score
	 * @return true if the score is an integer number, otherwise returns false
	 */
	private boolean scoreIsValid(String score){
		try{
			int number = Integer.parseInt(score);
			if (number < 0 || number > Integer.MAX_VALUE){
				return false;
			} 
			else{
				return true;
			}
		} catch(NumberFormatException e){
			return false;
		}
	}
}
