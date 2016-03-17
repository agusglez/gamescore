package com.king.gamescore.handler;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

/**
 * Interface for all the handlers
 */
public interface GameScoreHandler{

    void handle(HttpExchange httpExchange) throws IOException;
}
