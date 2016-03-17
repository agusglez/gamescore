package com.king.gamescore.session;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages user sessions
 * Singleton class
 */
public class SessionManager{

	private static final int SESSION_TIMEOUT = 600000; // Milliseconds

	private static SessionManager instance = null;
	private final static Object lock = new Object();

	private Map<Integer, Session> sessions;

	private SessionManager(){
		sessions = new ConcurrentHashMap<>();
	}

	/**
	 * Returns the singleton object
	 * @return the singleton object
	 */
	public static SessionManager getInstance(){
		
		if (instance == null){
			synchronized (lock){
				if (instance == null)
					instance = new SessionManager();
			}
		}
		return instance;
	}

	/**
	 * Creates a new session or updates the date of an existing session
	 * @param userId
	 * @return the user session
	 */
	public Session createSession(int userId){
		
		Session session = sessions.get(userId);
		if(session != null){
			session.setCreationDate(new Date());
		} else{
			String sessionKey = UUID.randomUUID().toString().replace("-", "");
			session = new Session(sessionKey, userId);
		}
		sessions.put(userId, session);
		return session;
	}

	/**
	 * Searches a session by the key and returns it
	 * @param sessionKey
	 * @return the session or null if the session does not exist
	 */
	public Session getSession(String sessionKey){
		
		Iterator<Map.Entry<Integer, Session>> it = sessions.entrySet().iterator();
		while(it.hasNext()){
			Session session = it.next().getValue();
			if(session.getSessionKey().equals(sessionKey)){
				return session;
			}
		}
		return null;
	}

	/**
	 * Checks if a session is valid
	 * @param sessionKey
	 * @return true if the session is valid, otherwise returns false
	 */
	public boolean sessionIsValid(String sessionKey){

		boolean sessionValid = false;
		Session session = this.getSession(sessionKey);
		if (session != null){
			Date now = new Date();
			if (now.getTime() - session.getCreationDate().getTime() < SESSION_TIMEOUT){
				sessionValid = true;				
			}
		}
		return sessionValid;
	}

	/**
	 * Searches a session by the sessionKey and removes it
	 * @param sessionKey
	 * @return true if the session is removed, otherwise returns false
	 */
	public boolean removeSession(String sessionKey){

		boolean removed = false;		
		Session session = this.getSession(sessionKey);
		if (session != null){
			Session sessionRemoved = sessions.remove(session.getUserId());
			if (sessionRemoved != null){
				removed = true;
			}
		}
		return removed;
	}
	
	public Map<Integer, Session> getSessions(){
		return sessions;
	}
}
