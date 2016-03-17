package com.king.gamescore;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import com.king.gamescore.score.ScoreRepository;

public class BackEndTest {
		
	private static final String URL_LOGIN = "http://localhost:8081/4711/login";
	private static final String URL_POST_SCORE2 = "http://localhost:8081/2/score?sessionkey=";
	private static final String URL_POST_SCORE3= "http://localhost:8081/3/score?sessionkey=";
	private static final String URL_HIGH_SCORE_LIST2 = "http://localhost:8081/2/highscorelist";
	private static final String URL_HIGH_SCORE_LIST3= "http://localhost:8081/3/highscorelist";
	private static final String URL_NO_SERVICE = "http://localhost:8081/noservice";
	private static final String SCORE = "1500";	
	private static final String WRONG_SCORE = "-1500";
	private static final String RESULT_HIGH_SCORE_LIST = "4711=1500";
	private static final String SESSION_NO_VALID = "XYZ";
	
	private static final int NUM_THREADS = 10; // must be smaller than SCORE_INT
	private static final int SCORE_INT = 1500;
	private static final int MAX_HIGH_SCORES = 15;


	@Test
	public void testLogin(){
		HttpResult httpResult = sendGET(URL_LOGIN);
		assertEquals(HttpsURLConnection.HTTP_OK, httpResult.getStatusCode());
	}
	
	@Test
	public void testPostScore(){
		HttpResult httpResultLogin = sendGET(URL_LOGIN);
		HttpResult httpResultPost = sendPOST(URL_POST_SCORE2 + httpResultLogin.getResponse(), SCORE);
		assertEquals(HttpsURLConnection.HTTP_OK, httpResultPost.getStatusCode());
	}
	
	@Test
	public void testPostWrongScore(){
		HttpResult httpResultLogin = sendGET(URL_LOGIN);
		HttpResult httpResultPost = sendPOST(URL_POST_SCORE2 + httpResultLogin.getResponse(), WRONG_SCORE);
		assertEquals(HttpsURLConnection.HTTP_BAD_REQUEST, httpResultPost.getStatusCode());
	}
	
	@Test
	public void testPostScoreSessionNoValid(){
		sendGET(URL_LOGIN);
		HttpResult httpResultPost = sendPOST(URL_POST_SCORE2 + SESSION_NO_VALID, SCORE);
		assertEquals(HttpsURLConnection.HTTP_UNAUTHORIZED, httpResultPost.getStatusCode());
	}
	
	@Test
	public void testHighScoreList(){
		ScoreRepository.getInstance().getScores().clear();
		HttpResult httpResultLogin = sendGET(URL_LOGIN);
		sendPOST(URL_POST_SCORE2 + httpResultLogin.getResponse(), SCORE);
		HttpResult httpResultList = sendGET(URL_HIGH_SCORE_LIST2);
		assertEquals(HttpsURLConnection.HTTP_OK, httpResultList.getStatusCode());
		assertEquals(RESULT_HIGH_SCORE_LIST, httpResultList.getResponse());
	}
	
	@Test
	public void testNoService(){
		HttpResult httpResult = sendGET(URL_NO_SERVICE);
		assertEquals(HttpsURLConnection.HTTP_BAD_REQUEST, httpResult.getStatusCode());
	}

	@Test
	//To run alone
	public void testWithThreads(){
		ScoreRepository.getInstance().getScores().clear();
		
		Set<Callable<Boolean>> callablesSet = new HashSet<Callable<Boolean>>();
		
		for (int i=0; i<NUM_THREADS; i++)
		{
			final int num = i;
			int score = SCORE_INT - num;
			
			callablesSet.add(new Callable<Boolean>()
			{
				@Override
				public Boolean call() throws Exception
				{
					HttpResult httpResultLogin = sendGET("http://localhost:8081/" + num + "/login");
					HttpResult httpResultPost = sendPOST(URL_POST_SCORE3 + httpResultLogin.getResponse(), Integer.valueOf(score).toString());
					
					return true;
				}                   
			});
		}

		ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
		List<Future<Boolean>> futures = null;
		try {
			futures = executorService.invokeAll(callablesSet);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (Future<Boolean> future : futures)
		{
			// do nothing for this test
		}
		executorService.shutdown();
		
		HttpResult httpResultList = sendGET(URL_HIGH_SCORE_LIST3);
		assertEquals(highScoreListExpectedWithThreads(), httpResultList.getResponse());
	}
	
	private String highScoreListExpectedWithThreads(){
		
		StringBuffer resultHighListScore = new StringBuffer();
		
		int limit = NUM_THREADS;
		if (limit > MAX_HIGH_SCORES){
			limit = MAX_HIGH_SCORES;
		}
		for (int i=0; i<limit; i++){
			int score = SCORE_INT - i;
			resultHighListScore.append(i).append("=").append(score);
			if (i != limit -1){
				resultHighListScore.append(",");
			}
		}
		
		return resultHighListScore.toString();
	}
	
	private HttpResult sendGET(String url) {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		HttpResult httpResult = new HttpResult();
		BufferedReader reader = null;
		
		try{
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

		reader = new BufferedReader(new InputStreamReader(
				httpResponse.getEntity().getContent()));

		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = reader.readLine()) != null) {
			response.append(inputLine);
		}

		httpResult.setResponse(response.toString());
		httpResult.setStatusCode(httpResponse.getStatusLine().getStatusCode());
		}
		catch (IOException e){
			e.printStackTrace();
		}
		finally{
			try {
				if (reader != null){
					reader.close();
				}
				if (httpClient != null){
					httpClient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}

		return httpResult;
	}

	private HttpResult sendPOST(String url, String requestBody) {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		HttpResult httpResult = new HttpResult();
		BufferedReader reader = null;

		try{
			HttpEntity entity = new StringEntity(requestBody);
			httpPost.setEntity(entity);
			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

			reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent()));

			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = reader.readLine()) != null) {
				response.append(inputLine);
			}

			httpResult.setResponse(response.toString());
			httpResult.setStatusCode(httpResponse.getStatusLine().getStatusCode());		
		}
		catch (IOException e){
			e.printStackTrace();
		}
		finally{
			try {
				if (reader != null){
					reader.close();
				}
				if (httpClient != null){
					httpClient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}

		return httpResult;
	}

	class HttpResult{

		private int statusCode;
		private String response;

		public int getStatusCode() {
			return statusCode;
		}
		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}
		public String getResponse() {
			return response;
		}
		public void setResponse(String response) {
			this.response = response;
		}
	}
}
