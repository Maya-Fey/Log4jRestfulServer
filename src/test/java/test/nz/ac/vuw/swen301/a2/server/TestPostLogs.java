package test.nz.ac.vuw.swen301.a2.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import nz.ac.vuw.swen301.a2.server.LogsServlet;

/**
 * @author Claire
 *
 */
public final class TestPostLogs {

	/**
	 * Testing, does invalid JSON result in an error?
	 */
	@Test
	public void testInvalidJSON() {
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setContentType("application/json");
        request.setContent("{ this is not valid json : 204 \" }".getBytes());

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST,response.getStatus());
	}
	
	/**
	 * Testing, does valid JSON without necessary attributes result in an error?
	 */
	@Test
	public void testValidJSONWithoutParam() {
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setContentType("application/json");
        
        JSONObject obj = new JSONObject();
        obj.accumulate("id", UUID.randomUUID());
        obj.accumulate("message", "This is a message.");
        obj.accumulate("timestamp", LogsServlet.format.format(new Date()));
        obj.accumulate("thread", "thisisathread");
        //obj.accumulate("logger", "path.to.logger");
        obj.accumulate("level", Level.DEBUG.toString());
        
        request.setContent(obj.toString().getBytes());

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST,response.getStatus());
        
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        request.setContentType("application/json");
        
        obj = new JSONObject();
        obj.accumulate("id", UUID.randomUUID());
        //obj.accumulate("message", "This is a message.");
        obj.accumulate("timestamp", LogsServlet.format.format(new Date()));
        obj.accumulate("thread", "thisisathread");
        obj.accumulate("logger", "path.to.logger");
        obj.accumulate("level", Level.DEBUG.toString());
        
        request.setContent(obj.toString().getBytes());

        service = new LogsServlet();
        service.doPost(request,response);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST,response.getStatus());
        
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        request.setContentType("application/json");
        
        obj = new JSONObject();
        obj.accumulate("id", UUID.randomUUID());
        obj.accumulate("message", "This is a message.");
        obj.accumulate("timestamp", LogsServlet.format.format(new Date()));
        //obj.accumulate("thread", "thisisathread");
        obj.accumulate("logger", "path.to.logger");
        obj.accumulate("level", Level.DEBUG.toString());
        
        request.setContent(obj.toString().getBytes());

        service = new LogsServlet();
        service.doPost(request,response);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST,response.getStatus());
	}
	
	/**
	 * Testing, does it process on a valid log?
	 */
	@Test
	public void testValidLog() {
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setContentType("application/json");
        
        JSONObject obj = TestHelper.newRandomLog(Level.DEBUG);
        request.setContent(obj.toString().getBytes());

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        assertEquals(HttpServletResponse.SC_CREATED,response.getStatus());
	}
	
	/**
	 * Testing, does it process on a valid log?
	 */
	@Test
	public void testBadContentType() {
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setContentType("text/plain");
        
        JSONObject obj = TestHelper.newRandomLog(Level.TRACE);
        request.setContent(obj.toString().getBytes());

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST,response.getStatus());
	}
	
	/**
	 * Testing, will it fail on duplicate logs?
	 */
	@Test
	public void testDuplicateDetection() {
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setContentType("application/json");
        
        JSONObject obj = TestHelper.newRandomLog(Level.DEBUG);
        request.setContent(obj.toString().getBytes());

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);
        
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        request.setContentType("application/json");
        request.setContent(obj.toString().getBytes());
        
        service.doPost(request,response);

        assertEquals(HttpServletResponse.SC_CONFLICT,response.getStatus());
	}
	
	/**
	 * Testing, does an invalid UUID result in an error?
	 */
	@Test
	public void testBadUUID() {
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setContentType("application/json");
        
        JSONObject obj = new JSONObject();
        obj.accumulate("id", UUID.randomUUID() + " ");
        obj.accumulate("message", "This is a message.");
        obj.accumulate("timestamp", LogsServlet.format.format(new Date()));
        obj.accumulate("thread", "thisisathread");
        obj.accumulate("logger", "path.to.logger");
        obj.accumulate("level", Level.DEBUG.toString());
        
        request.setContent(obj.toString().getBytes());

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST,response.getStatus());
	}
	
	/**
	 * Testing, does an invalid timestamp result in an error	
	 */
	@Test
	public void testBadTimestamp() {
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setContentType("application/json");
        
        JSONObject obj = new JSONObject();
        obj.accumulate("id", UUID.randomUUID());
        obj.accumulate("message", "This is a message.");
        obj.accumulate("timestamp", "X" + LogsServlet.format.format(new Date()));
        obj.accumulate("thread", "thisisathread");
        obj.accumulate("logger", "path.to.logger");
        obj.accumulate("level", Level.DEBUG.toString());
        
        request.setContent(obj.toString().getBytes());

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST,response.getStatus());
	}
	
	/**
	 * Testing, does an invalid level result in an error
	 */
	@Test
	public void testBadLevel() {
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setContentType("application/json");
        
        JSONObject obj = new JSONObject();
        obj.accumulate("id", UUID.randomUUID());
        obj.accumulate("message", "This is a message.");
        obj.accumulate("timestamp", LogsServlet.format.format(new Date()));
        obj.accumulate("thread", "thisisathread");
        obj.accumulate("logger", "path.to.logger");
        obj.accumulate("level", "INVALID");
        
        request.setContent(obj.toString().getBytes());

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST,response.getStatus());
	}
}
