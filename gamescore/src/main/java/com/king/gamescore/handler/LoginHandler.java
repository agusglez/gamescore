package com.king.gamescore.handler;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.king.gamescore.session.SessionManager;
import com.sun.net.httpserver.HttpExchange;

/**
 * Handler to manage login requests
 */
public class LoginHandler implements GameScoreHandler{

	private static final Logger logger = LogManager.getLogger(LoginHandler.class.getName());

    private int userId;

    public LoginHandler(int userId){
        this.userId = userId;
    }

    public void handle(HttpExchange httpExchange) throws IOException{
    	
        logger.info("Login begin call. User: " + this.userId);
       
        String sessionKey = SessionManager.getInstance().createSession(userId).getSessionKey();
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, sessionKey.length());
        httpExchange.getResponseBody().write(sessionKey.getBytes());
		
        logger.info("Login end call. User: " + this.userId + ". Result: " + sessionKey);
    }
}
