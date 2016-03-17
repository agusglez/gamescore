package com.king.gamescore.score;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Scores repository
 * Singleton class
 */
public class ScoreRepository{

	private static volatile ScoreRepository instance = null;
	private final static Object lock = new Object();

	private ConcurrentHashMap<Integer, ConcurrentSkipListSet<Score>> scores;

	private ScoreRepository(){
		scores = new ConcurrentHashMap<>();
	}

	/**
	 * Returns the singleton object
	 * @return the singleton object
	 */
	public static ScoreRepository getInstance(){
		if (instance == null){
			synchronized (lock){
				if (instance == null)
					instance = new ScoreRepository();
			}
		}
		return instance;
	}

	/**
	 * Puts a score in the repository. The score is associated to a level
	 * 
	 * @param levelId 
	 * @param score
	 */
	public synchronized void putScore(Integer levelId, Score score){
		// Must be thread safe
		ConcurrentSkipListSet<Score> concurrentSkipListSet = scores.get(levelId);
		if(concurrentSkipListSet != null){
			concurrentSkipListSet.add(score);
		} 
		else{
			concurrentSkipListSet = new ConcurrentSkipListSet<>(new ScoreComparator());
			concurrentSkipListSet.add(score);
			scores.put(levelId, concurrentSkipListSet);
		}
		
	}

	/**
	 * Returns the high score list for a level
	 *
	 * @param levelId
	 * @param max is the maximum number of scores
	 * @return the high score list for a level in format userId=score,userId=score...
	 */
	public String getHighScoreList(Integer levelId, int max){
		String highScoreList = "";  // response must be an empty string if no scores
		ConcurrentSkipListSet<Score> concurrentSkipListSet = scores.get(levelId);
		if(concurrentSkipListSet != null){
			Iterator<Score> it = concurrentSkipListSet.descendingIterator();
			StringBuffer buffer = new StringBuffer();
			Set<String> usersInList = new HashSet<>();
			int i = 1;
			while(it.hasNext() && i <= max){
				Score score = it.next();
				if (usersInList.add(score.getUserId())){
					buffer.append(score.getUserId());
					buffer.append("=");
					buffer.append(score.getScore());
					if (i != max && it.hasNext()){
						buffer.append(",");
					}
					i++;
				}
			}            
			highScoreList = buffer.toString();
		}
		return highScoreList;
	}
	
	public ConcurrentHashMap<Integer, ConcurrentSkipListSet<Score>> getScores(){
		return scores;
	}
}
