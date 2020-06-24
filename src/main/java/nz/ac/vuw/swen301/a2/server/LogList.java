package nz.ac.vuw.swen301.a2.server;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Stores oncoming logs for later retrieval
 * @author Claire
 */
public class LogList {
	
	private static final Comparator<JSONObject> comp = new Comparator<JSONObject>() {

		private DateFormat formatter = LogsServlet.timestampFormatter();
		
		@Override
		public int compare(JSONObject o1, JSONObject o2) {
			try {
				return formatter.parse(o2.getString("timestamp")).compareTo(formatter.parse(o1.getString("timestamp")));
			} catch(Exception e) { throw new Error(e); }
		}
		
	};
	
	private final List<JSONObject> logs = new ArrayList<>();
	private final Map<String, JSONObject> idMap = new HashMap<>();

	/**
	 * Adds the event to the list 
	 * 
	 * @param obj
	 */
	public void addEvent(JSONObject obj) {
		idMap.put(obj.getString("id"), obj);
		logs.add(obj);
		Collections.sort(logs, comp);
	}
	
	/**
	 * @param obj
	 * @return Whether this object already exists in the list
	 */
	public boolean exists(JSONObject obj) {
		return idMap.containsKey(obj.getString("id"));
	}
	
	/**
	 * @param level
	 * @param limit
	 * @return An array with logs at that level, up to a certain amount
	 */
	public JSONArray getLogs(Level level, int limit) {
		JSONArray array = new JSONArray();
		int i, j; i = j = 0;
		while(i < limit && j < logs.size()) {
			JSONObject log = logs.get(j);
			if(Level.toLevel(log.getString("level")).toInt() >= level.toInt()) {
				array.put(log);
				i++;
			}
			j++;
		}
		return array;
	}

}
