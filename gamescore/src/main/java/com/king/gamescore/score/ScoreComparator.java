package com.king.gamescore.score;

import java.util.Comparator;

/**
 * Compares two Score objects
 */
public class ScoreComparator implements Comparator<Score>{

    @Override
    public int compare(Score scoreOne, Score scoreTwo){
        return (scoreOne.getScore() < scoreTwo.getScore() ) ? -1 : (scoreOne.getScore() > scoreTwo.getScore() ) ? 1 : 0 ;
        //More simple:
        //return scoreOne.getScore().compareTo(scoreTwo.getScore());
    }
}
