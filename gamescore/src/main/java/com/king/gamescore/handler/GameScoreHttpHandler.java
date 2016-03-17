package com.king.gamescore.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Front controller responsible for dispatching the requests to the corresponding handler
 */
public class GameScoreHttpHandler implements HttpHandler{

	private static final Logger logger = LogManager.getLogger(GameScoreHttpHandler.class.getName());

	public void handle(HttpExchange httpExchange) throws IOException{

		try{
			GameScoreHandler handler = this.getHandler(httpExchange);
			if (handler != null){
				handler.handle(httpExchange);
			}
			else{
				logger.warn("No service for the request: " + httpExchange.getRequestURI().toString());
				httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, -1);
			}

		} finally{
			httpExchange.getResponseBody().close();
		}
	}

	/**
	 * Returns the handler to manage the request
	 * @param httpExchange
	 * @return handler to manage the request, null if the requested URI is not correct
	 */
	private GameScoreHandler getHandler(HttpExchange httpExchange){

		Pattern loginPattern = Pattern.compile("/\\d+/login");
		Pattern userScoreLevelPattern = Pattern.compile("/\\d+/score\\?sessionkey=\\w+");
		Pattern highScoreListPattern = Pattern.compile("/\\d+/highscorelist");

		String requestUri = httpExchange.getRequestURI().toString();
		
		int id;
		try{
			id = Integer.parseInt(requestUri.split("/")[1]);
		}
		catch(NumberFormatException e){
			return null;
		}

		if (loginPattern.matcher(requestUri).matches()){
			return new LoginHandler(id);
		}

		if (userScoreLevelPattern.matcher(requestUri).matches()){
			String sessionKey = requestUri.split("=")[1];
			String score = this.getScoreFromRequest(httpExchange);
			return new UserScoreLevelHandler(id, sessionKey, score);
		}

		if (highScoreListPattern.matcher(requestUri).matches()){
			return new HighScoreListHandler(id);
		}

		return null;       
	}

	/**
	 * Returns the score from the request body
	 * @param httpExchange
	 * @return the score
	 */
	private String getScoreFromRequest(HttpExchange httpExchange){

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				httpExchange.getRequestBody()));

		StringBuilder score = new StringBuilder();
		try{
			score.append(reader.readLine());
		} 
		catch (IOException e){
			logger.error("Error reading the score: " + e.getMessage());
		}
		finally{
			try{
				if (reader != null){
					reader.close();
				}
			} catch(IOException e){
				logger.error("Error reading the score: " + e.getMessage());
			}
		}

		return score.toString();
	}
}
