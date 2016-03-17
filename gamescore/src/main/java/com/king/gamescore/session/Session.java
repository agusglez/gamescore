package com.king.gamescore.session;

import java.util.Date;

/**
 * Represents a user session
 */
public class Session{
	
    private final String sessionKey;
	private final Integer userId;
    private Date creationDate;

    public Session(String sessionKey, Integer userId){
    	this.sessionKey = sessionKey;
    	this.userId = userId;        
        this.creationDate = new Date();
    }

    public String getSessionKey(){
        return sessionKey;
    }
    
    public Integer getUserId(){
        return userId;
    }

    public Date getCreationDate(){
        return creationDate;
    }

    public void setCreationDate(Date creationDate){
        this.creationDate = creationDate;
    } 
}
