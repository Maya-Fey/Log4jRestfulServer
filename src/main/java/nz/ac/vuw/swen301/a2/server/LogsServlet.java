/**
 * 
 */
package nz.ac.vuw.swen301.a2.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Predicate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author Claire
 *
 */
public class LogsServlet extends HttpServlet {

	private static final long serialVersionUID = 3441175578112383791L;
	
	/**
	 * @return Formatter for this servlet
	 */
	public static final DateFormat timestampFormatter() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		return format;
	}
	
	static LogList list = new LogList();
	
	private static void reset() { list = new LogList(); }
	
	/**
	 * 
	 */
	public LogsServlet()
	{
		reset();
	}
	
	private final DateFormat format = timestampFormatter();
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	{
		resp.setContentType("application/json");
		Map<String, String[]> params = req.getParameterMap();
		
		int max = 0;
		Level level = null;
		
		max = getInt32(resp, params, "limit", (i) -> { return i >= 0; });
		if(resp.getStatus() == HttpServletResponse.SC_BAD_REQUEST) return;
		level = getLevel(resp, params, "level");
		if(resp.getStatus() == HttpServletResponse.SC_BAD_REQUEST) return;
		
		JSONArray ret = list.getLogs(level, max);
		
		try(PrintWriter writer = resp.getWriter()) {
			writer.write(ret.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	{
		if(!req.getContentType().equals("application/json")) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		try {
			JSONTokener token = new JSONTokener(req.getReader());
			JSONObject obj = new JSONObject(token);
			
			UUID.fromString(obj.getString("id"));
			format.parse(obj.getString("timestamp"));
			if(!obj.getString("level").equals(Level.DEBUG.toString())) {
				if(Level.toLevel(obj.getString("level")) == Level.DEBUG) {
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}
			}
			
			if(!obj.has("message") || !obj.has("thread") || !obj.has("logger")) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			
			if(list.exists(obj)) {
				resp.setStatus(HttpServletResponse.SC_CONFLICT);
				return;
			}
			
			list.addEvent(obj);
			resp.setStatus(HttpServletResponse.SC_CREATED);
		} catch(IOException e) {
			e.printStackTrace();
		} catch(JSONException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		} catch(IllegalArgumentException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		} catch (ParseException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
	}
	
	private static int getInt32(HttpServletResponse resp, Map<String, String[]> params, String param, Predicate<Integer> acceptanceCriteria) {
		String[] vals = params.get(param);
		if(vals == null) {
			fail(resp, "Parameter " + param + " undefined"); return 0;
		}
		if(vals.length > 1) {
			fail(resp, "Parameter " + param + " defined more than once"); return 0;
		}
		try {
			int number = Integer.parseInt(vals[0]);
			if(!acceptanceCriteria.test(number)) { 
				fail(resp, "Paramater " + param + " parsed correctly but did not meet acceptance criteria"); return 0;
			}
			return number;
		} catch(NumberFormatException e) {
			fail(resp, "Parameter " + param + " was not a valid 32-bit integer as expected."); return 0; 
		}
	}
	
	private static Level getLevel(HttpServletResponse resp, Map<String, String[]> params, String param) {
		String[] vals = params.get(param);
		if(vals == null) {
			fail(resp, "Parameter " + param + " undefined"); return null;
		}
		if(vals.length > 1) {
			fail(resp, "Parameter " + param + " defined more than once"); return null;
		}
		if(vals[0].equals(Level.DEBUG.toString())) return Level.DEBUG;
		Level level = Level.toLevel(vals[0]);
		if(level != Level.DEBUG) return level;
		fail(resp, "Parameter " + param + " was not a valid level."); return null;
	}
	
	private static void fail(HttpServletResponse resp, String reason) {
		resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		JSONObject obj = new JSONObject();
		obj.accumulate("error", reason);
		try(PrintWriter writer = resp.getWriter()) {
			writer.write(obj.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
