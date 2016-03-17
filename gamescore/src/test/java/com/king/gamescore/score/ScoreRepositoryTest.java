package com.king.gamescore.score;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ConcurrentSkipListSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ScoreRepositoryTest {
	
	private static final String USER_ID = "471";
	private static final String USER_ID2 = "472";
	private static final Integer LEVEL_ID = 2;
	private static final Integer SCORE = 1500;
	private static final Integer SCORE2 = 1501;

	private ScoreRepository scoreRepository;
	
	@Before
	public void setUp() throws Exception {
		scoreRepository = ScoreRepository.getInstance();
	}
	
	@After
	public void clear() throws Exception {
		scoreRepository.getScores().clear();
	}

	@Test
	public void testPutScore() {
		Score score = new Score(SCORE, USER_ID);
		scoreRepository.putScore(LEVEL_ID, score);
		ConcurrentSkipListSet<Score> concurrentSkipListSet = scoreRepository.getScores().get(LEVEL_ID);
		assertNotNull(concurrentSkipListSet);
		assertNotNull(scoreRepository.getScores());
		assertTrue(concurrentSkipListSet.contains(score));
		assertTrue(scoreRepository.getScores().containsKey(LEVEL_ID));
	}
	
	@Test
	public void testGetHighScoreList() {
		Score score = new Score(SCORE, USER_ID);
		Score score2 = new Score(SCORE2, USER_ID2);
		ConcurrentSkipListSet<Score> concurrentSkipListSet = new ConcurrentSkipListSet<>(new ScoreComparator());
		concurrentSkipListSet.add(score);
		concurrentSkipListSet.add(score2);
		scoreRepository.getScores().put(LEVEL_ID, concurrentSkipListSet);
		assertEquals(USER_ID2 + "=" + SCORE2 + "," + USER_ID + "=" + SCORE, scoreRepository.getHighScoreList(LEVEL_ID, 2));
	}
	
	@Test
	public void testGetHighScoreListLimited() {
		Score score = new Score(SCORE, USER_ID);
		Score score2 = new Score(SCORE2, USER_ID2);
		ConcurrentSkipListSet<Score> concurrentSkipListSet = new ConcurrentSkipListSet<>(new ScoreComparator());
		concurrentSkipListSet.add(score);
		concurrentSkipListSet.add(score2);
		scoreRepository.getScores().put(LEVEL_ID, concurrentSkipListSet);
		assertEquals(USER_ID2 + "=" + SCORE2, scoreRepository.getHighScoreList(LEVEL_ID, 1)); // only 1 result
	}
	
	@Test
	public void testGetHighScoreListNoScores() {
		assertEquals("", scoreRepository.getHighScoreList(LEVEL_ID, 2));
	}

}
