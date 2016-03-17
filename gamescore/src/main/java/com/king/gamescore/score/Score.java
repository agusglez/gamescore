package com.king.gamescore.score;

/**
 * Represents a score made for a user for a level
 */
public class Score{
    
    private Integer score;
    private String userId;

    public Score(Integer score, String userId){
    	this.score = score;
        this.userId = userId;        
    }

    public Integer getScore(){
        return score;
    }
    
    public String getUserId(){
        return userId;
    }    
}
