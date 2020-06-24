/**
 * 
 */
package nz.ac.vuw.swen301.a2.server;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.log4j.Level;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An index of log events. Indexes logs by:  <br>
 *    - day                                  <br>
 *    - log level                            <br>
 *    - thread                               <br>
 *    - logger
 *    
 * @author Claire
 */
public class LogIndex {
	
	private final Map<String, Set<JSONObject>> dateIndex = new HashMap<>();
	private final Map<String, Set<JSONObject>> threadIndex = new HashMap<>();
	private final Map<String, Set<JSONObject>> loggerIndex = new HashMap<>();
	private final Map<Level, Set<JSONObject>> levelIndex = new HashMap<>();
	
	/**
	 * @param logs The list of logs you'd like to index
	 */
	public LogIndex(JSONArray logs)
	{
		DateFormat format = LogsServlet.timestampFormatter();
		logs.forEach((o) -> {
			JSONObject obj = (JSONObject) o;
			try {
				addToSetMap(dateIndex, removeTime(format.parse(obj.getString("timestamp"))), obj);
				addToSetMap(threadIndex, obj.getString("thread"), obj);
				addToSetMap(loggerIndex, obj.getString("logger"), obj);
				addToSetMap(levelIndex, Level.toLevel(obj.getString("level")), obj);
			} catch (ParseException e) {
				throw new Error(e);
			} 
		});
	}
	
	/**
	 * @return The columns for a new table
	 */
	public Set<String> getCols()
	{
		return dateIndex.keySet();
	}
	
	/**
	 * @return The rows for this table
	 */
	public Set<Object> getRows()
	{
		Set<Object> set = new HashSet<>();
		set.addAll(threadIndex.keySet());
		set.addAll(loggerIndex.keySet());
		set.addAll(levelIndex.keySet());
		return set;
	}
	
	/**
	 * @param row The row, as a date object
	 * @param col The column, as a thread, logger, or level
	 * @return The value at that position
	 */
	public int getValAt(Object row, Object col)
	{
		Set<JSONObject> set1;
		if(threadIndex.containsKey(row))
			set1 = threadIndex.get(row);
		else if(loggerIndex.containsKey(row))
			set1 = loggerIndex.get(row);
		else
			set1 = levelIndex.get(row);
		Set<JSONObject> set2 = dateIndex.get(col);
		return sizeOfIntersection(set1, set2);
	}
	
	/**
	 * @return An HTML table with the data contained by this index
	 */
	public String toHTMLTable()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<table>\n");
		builder.append("\t<thead>\n");
		builder.append("\t\t<tr>\n");
		builder.append("\t\t\t<td>\n");
		builder.append("\t\t\t</td>\n");
		for(String date : this.getCols())
		{
			builder.append("\t\t\t<td>\n");
			builder.append("\t\t\t\t");
			builder.append(date);
			builder.append("\n");
			builder.append("\t\t\t</td>\n");
		}
		builder.append("\t\t</tr>\n");
		builder.append("\t</thead>\n");
		builder.append("\t<tbody>\n");
		for(Object o : this.getRows())
		{
			builder.append("\t\t<tr>\n");
			builder.append("\t\t\t<td>\n");
			builder.append("\t\t\t\t");
			builder.append(o.toString());
			builder.append("\n");
			builder.append("\t\t\t</td>\n");
			for(String date : this.getCols())
			{
				builder.append("\t\t\t<td>\n");
				builder.append("\t\t\t\t");
				builder.append(this.getValAt(o, date));
				builder.append("\n");
				builder.append("\t\t\t</td>\n");
			}
			builder.append("\t\t</tr>\n");
		}
		builder.append("\t</tbody>\n");
		builder.append("</table>\n");
		return builder.toString();
	}
	
	/**
	 * @return An HTML table with the data contained by this index
	 */
	public String toCSV()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("\"\"\t");
		for(String date : this.getCols())
		{
			builder.append(date);
			builder.append("\t");
		}
		builder.delete(builder.length() - 1, builder.length());
		builder.append("\n");
		for(Object o : this.getRows())
		{
			builder.append(o.toString());
			builder.append("\t");
			for(String date : this.getCols())
			{
				builder.append(this.getValAt(o, date));
				builder.append("\t");
			}
			builder.delete(builder.length() - 1, builder.length());
			builder.append("\n");
		}
		builder.delete(builder.length() - 1, builder.length());
		return builder.toString();
	}
	
	/**
	 * @return An XLS workbook
	 */
	public Workbook toXLS()
	{
		Workbook workbook = new HSSFWorkbook(); 
        Sheet sheet = workbook.createSheet("Status");
        int nRow = 0;
        int nCell = 0;
        Row header = sheet.createRow(nRow++);
        header.createCell(nCell++);
        for(String date : this.getCols())
		{
        	header.createCell(nCell++).setCellValue(date);
		}
        for(Object o : this.getRows())
		{
        	nCell = 0;
        	Row row = sheet.createRow(nRow++);
        	row.createCell(nCell++).setCellValue(o.toString());
			for(String date : this.getCols())
			{
				row.createCell(nCell++).setCellValue(this.getValAt(o, date));
			}
		}
        return workbook;
	}
	
	private static <Key, Value> void addToSetMap(Map<Key, Set<Value>> map, Key key, Value value)
	{
		if(!map.containsKey(key))
			map.put(key, new HashSet<Value>());
		map.get(key).add(value);
	}
	
	/** 
	 * @param set1 The first set
	 * @param set2 The second set
	 * @return The number of shared elements between each set
	 */
	public static <T> int sizeOfIntersection(Set<T> set1, Set<T> set2)
	{
		Set<T> set3 = new HashSet<T>(set1);
		set3.retainAll(set2);
		return set3.size();
	}
	
	/**
	 * A date format that only includes day/month/year
	 */
	public final DateFormat format = new SimpleDateFormat("d/M/YYYY"); { format.setTimeZone(TimeZone.getTimeZone("UTC")); }
	
	/**
	 * @param date A fully-fledged date
	 * @return A date with only a year, month, and day
	 */
	public String removeTime(Date date)
	{
		return format.format(date);
	}

}
