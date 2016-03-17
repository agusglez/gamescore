/**
 * 
 */
package com.king.gamescore.session;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class GameSessionTest {

	private static final Integer USER_ID = 471;
	
	private SessionManager sessionManager;

	@Before
	public void setUp() throws Exception {
		sessionManager = SessionManager.getInstance();
	}

	@Test
	public void testCreateSession() {
		Session session = sessionManager.createSession(USER_ID);
		assertNotNull(session);
		assertNotNull(session.getUserId());
		assertNotNull(session.getSessionKey());
		assertNotNull(session.getCreationDate());
		assertEquals(USER_ID, session.getUserId());
		assertTrue(sessionManager.getSessions().containsKey(USER_ID));
		assertTrue(sessionManager.getSessions().containsValue(session));
	}
	
	@Test
	public void testGetSession() {
		String sessionKey = UUID.randomUUID().toString().replace("-", "");
		Session sessionBefore = new Session(sessionKey, USER_ID);
		sessionManager.getSessions().put(USER_ID, sessionBefore);
		Session sessionAfter = sessionManager.getSession(sessionKey);
		assertSame(sessionBefore, sessionAfter);		
	}
	
	@Test
	public void testSessionIsValid() {
		String sessionKey = UUID.randomUUID().toString().replace("-", "");
		Session session = new Session(sessionKey, USER_ID);
		sessionManager.getSessions().put(USER_ID, session);
		assertTrue(sessionManager.sessionIsValid(sessionKey));
		sessionManager.getSessions().get(USER_ID).setCreationDate(new Date(System.currentTimeMillis() - 6000000));
		assertFalse(sessionManager.sessionIsValid(sessionKey));
	}
	
	@Test
	public void testRemoveSession() {
		String sessionKey = UUID.randomUUID().toString().replace("-", "");
		Session session = new Session(sessionKey, USER_ID);
		sessionManager.getSessions().put(USER_ID, session);
		sessionManager.removeSession(sessionKey);
		assertNull(sessionManager.getSessions().get(USER_ID));
	}

}
