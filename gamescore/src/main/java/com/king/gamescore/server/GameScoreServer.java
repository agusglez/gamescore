package com.king.gamescore.server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.king.gamescore.handler.GameScoreHttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * HTTP Server which registers game scores for different users and levels, 
 * with the capability to return high score lists per level
 */
public class GameScoreServer{

	private final static int NTHREADS = 50;
	
    private HttpServer httpServer;
    private ExecutorService executorService;

    /**
     * Creates the server
     * @throws IOException 
     */
    public GameScoreServer() throws IOException{

        httpServer = HttpServer.create(new InetSocketAddress(8081), 0);
        httpServer.createContext("/", new GameScoreHttpHandler());
        executorService = Executors.newFixedThreadPool(NTHREADS);
        httpServer.setExecutor(executorService);
    }

    /**
     * Starts the server
     */
    public void start(){
    	
        httpServer.start();
    }

    /**
     * Stops the server
     */
    public void stop(){
    	
        if (httpServer != null){
            httpServer.stop(0);
            executorService.shutdown();
        }
    }
}
