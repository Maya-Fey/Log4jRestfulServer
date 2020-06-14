package test.nz.ac.vuw.swen301.a2.server;

import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Level;
import org.json.JSONObject;

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
		obj.accumulate("level", level);
		obj.accumulate("message", "This is a message " + Math.random());
		obj.accumulate("logger", "This is a logger " + Math.random());
		obj.accumulate("thread", "This is a thread " + Math.random());
		obj.accumulate("timestamp", LogsServlet.format.format(new Date()));
		obj.accumulate("id", UUID.randomUUID());
		return obj;
	}

}
