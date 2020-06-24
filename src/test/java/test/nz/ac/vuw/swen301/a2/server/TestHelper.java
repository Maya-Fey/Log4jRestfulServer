package test.nz.ac.vuw.swen301.a2.server;

import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Level;
import org.json.JSONObject;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import nz.ac.vuw.swen301.a2.server.LogsServlet;

/**
 * @author Claire
 *
 */
public class TestHelper {
	
	/**
	 * @param level
	 * @return A random log at the specified level
	 */
	public static JSONObject newRandomLog(Level level) {
		JSONObject obj = new JSONObject();
		obj.accumulate("level", level.toString());
		obj.accumulate("message", "This is a message " + Math.random());
		obj.accumulate("logger", "This is a logger " + Math.random());
		obj.accumulate("thread", "This is a thread " + Math.random());
		obj.accumulate("timestamp", LogsServlet.format.format(new Date()));
		obj.accumulate("id", UUID.randomUUID());
		return obj;
	}
	
	/**
	 * @param level
	 * @param logger 
	 * @param thread 
	 * @param date 
	 * @return A random log at the specified level
	 */
	public static JSONObject newRandomLog(Level level, String logger, String thread, Date date) {
		JSONObject obj = new JSONObject();
		obj.accumulate("level", level.toString());
		obj.accumulate("message", "This is a message " + Math.random());
		obj.accumulate("logger", logger);
		obj.accumulate("thread", thread);
		obj.accumulate("timestamp", LogsServlet.format.format(date));
		obj.accumulate("id", UUID.randomUUID());
		return obj;
	}
	
	/**
	 * Testing, will it fail on duplicate logs?
	 * @param servlet 
	 * @param nLogs 
	 * @param level 
	 * @param logger 
	 * @param thread 
	 * @param date 
	 */
	public static void addLogs(LogsServlet servlet, int nLogs, Level level, String logger, String thread, Date date) {
		while(nLogs-- > 0)
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
	        MockHttpServletResponse response = new MockHttpServletResponse();
	        request.setContentType("application/json");
	        JSONObject obj = TestHelper.newRandomLog(level, logger, thread, new Date(date.getTime() + nLogs));
	        request.setContent(obj.toString().getBytes());
	        servlet.doPost(request, response);
		}
	}

}
