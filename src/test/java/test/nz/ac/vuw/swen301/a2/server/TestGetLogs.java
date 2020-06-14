/**
 * 
 */
package test.nz.ac.vuw.swen301.a2.server;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import nz.ac.vuw.swen301.a2.server.LogsServlet;

/**
 * @author Claire
 */
public final class TestGetLogs {

	/**
	 * Testing, does no parameters cause an error?
	 */
	@Test
	public void testNoParams() {
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doGet(request,response);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST,response.getStatus());
	}
	
	/**
	 * Testing, does missing one param cause an error?
	 */
	@Test
	public void testMissingOne() {
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
	        MockHttpServletResponse response = new MockHttpServletResponse();
	        request.addParameter("limit", Integer.toString(10));
	        
	        LogsServlet service = new LogsServlet();
	        service.doGet(request,response);
	
	        assertEquals(HttpServletResponse.SC_BAD_REQUEST,response.getStatus());
		}
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
	        MockHttpServletResponse response = new MockHttpServletResponse();
	        request.addParameter("level", "DEBUG");
	        
	        LogsServlet service = new LogsServlet();
	        service.doGet(request,response);
	
	        assertEquals(HttpServletResponse.SC_BAD_REQUEST,response.getStatus());
		}
	}
	
	/**
	 * Testing, does missing one param cause an error?
	 */
	@Test
	public void testPlusOne() {
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
	        MockHttpServletResponse response = new MockHttpServletResponse();
	        request.addParameter("limit", Integer.toString(10));
	        request.addParameter("level", "DEBUG");
	        request.addParameter("level", "DEBUG");
	        
	        LogsServlet service = new LogsServlet();
	        service.doGet(request,response);
	
	        assertEquals(HttpServletResponse.SC_BAD_REQUEST,response.getStatus());
		}
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
	        MockHttpServletResponse response = new MockHttpServletResponse();
	        request.addParameter("limit", Integer.toString(10));
	        request.addParameter("limit", Integer.toString(10));
	        request.addParameter("level", "DEBUG");
	        
	        LogsServlet service = new LogsServlet();
	        service.doGet(request,response);
	
	        assertEquals(HttpServletResponse.SC_BAD_REQUEST,response.getStatus());
		}
	}
	
	
	/**
	 * Testing, does no parameters cause an error?
	 */
	@Test
	public void testValidRequest() {
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addParameter("limit", Integer.toString(10));
        request.addParameter("level", "DEBUG");
        
        LogsServlet service = new LogsServlet();
        service.doGet(request,response);

        assertEquals(HttpServletResponse.SC_OK,response.getStatus());
	}
	
	/**
	 * Testing, does a negative limit cause an error
	 */
	@Test
	public void testNegativeLimit() {
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addParameter("limit", Integer.toString(-10));
        request.addParameter("level", "TRACE");
        
        LogsServlet service = new LogsServlet();
        service.doGet(request,response);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST,response.getStatus());
	}
	
	/**
	 * Testing, does a negative limit cause an error
	 */
	@Test
	public void testInvalidLimit() {
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addParameter("limit", "invalid");
        request.addParameter("level", "TRACE");
        
        LogsServlet service = new LogsServlet();
        service.doGet(request,response);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST,response.getStatus());
	}
	
	/**
	 * Testing, does an invalid level cause an error
	 */
	@Test
	public void testInvalidLevel() {
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addParameter("limit", Integer.toString(10));
        request.addParameter("level", "INVALID");
        
        LogsServlet service = new LogsServlet();
        service.doGet(request,response);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST,response.getStatus());
	}
	
	/**
	 * Testing that returning the logs works
	 */
	@Test
	public void testValidRequestResponse() {
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addParameter("limit", Integer.toString(10));
        request.addParameter("level", "DEBUG");
        
        LogsServlet service = new LogsServlet();
        
        JSONObject warn = addRandLog(Level.WARN, service);
        JSONObject debug = addRandLog(Level.DEBUG, service);
        addRandLog(Level.TRACE, service);
        
        service.doGet(request,response);

        assertEquals(HttpServletResponse.SC_OK,response.getStatus());
        
        JSONArray arr = fromResponse(response);
        assertEquals(2, arr.length());
        assertEquals(arr.get(0).toString(), ((JSONObject) arr.get(0)).getString("level").equals("WARN") ? warn.toString() : debug.toString());
	}
	
	/**
	 * Testing that the limit works
	 */
	@Test
	public void testLimit() {
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addParameter("limit", Integer.toString(10));
        request.addParameter("level", Level.ALL.toString());
        
        LogsServlet service = new LogsServlet();
        
        for(int i = 0; i < 20; i++) {
        	synchronized(service) {
        		try {
					service.wait(1);
				} catch (InterruptedException e) {}
        	}
        	addRandLog(Level.TRACE, service);
        }
        
        service.doGet(request,response);

        assertEquals(HttpServletResponse.SC_OK,response.getStatus());
        
        JSONArray arr = fromResponse(response);
        assertEquals(10, arr.length());
    }
	
	private static JSONObject addRandLog(Level level, LogsServlet service) {
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setContentType("application/json");
        
        JSONObject obj = TestHelper.newRandomLog(level);
        request.setContent(obj.toString().getBytes());

        service.doPost(request,response);
        
        return obj;
	}
	
	private static JSONArray fromResponse(MockHttpServletResponse resp) {
		try {
			return new JSONArray(new JSONTokener(resp.getContentAsString()));
		} catch (Exception e) {
			throw new Error(e);
		}
	}
	
}
