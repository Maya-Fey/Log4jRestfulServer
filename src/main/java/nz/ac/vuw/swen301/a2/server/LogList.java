package nz.ac.vuw.swen301.a2.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
		Iterator<JSONObject> filtered = logs.stream().filter((obj) -> {
			return Level.toLevel(obj.getString("level")).toInt() >= level.toInt();
		}).iterator();
		for(int i = 0; i < limit && filtered.hasNext(); i++) {
			array.put(filtered.next());
		}
		return array;
	}

}
