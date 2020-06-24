/**
 * 
 */
package test.nz.ac.vuw.swen301.a2.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Level;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import nz.ac.vuw.swen301.a2.server.LogsServlet;
import nz.ac.vuw.swen301.a2.server.StatsCSVServlet;

/**
 * @author Claire
 */
public class TestStatsCSV {

	/**
	 * 
	 */
	public final DateFormat format = new SimpleDateFormat("d/M/YYYY"); { format.setTimeZone(TimeZone.getTimeZone("UTC")); }
	
	/**
	 * @throws IOException 
	 * 
	 */
	@Test
	public void comprehensiveTest() throws IOException
	{
		LogsServlet servlet = new LogsServlet();
		StatsCSVServlet stats = new StatsCSVServlet();
		TestHelper.addLogs(servlet, 2, Level.FATAL, "mylogger2", "mythread", new Date());
		TestHelper.addLogs(servlet, 5, Level.FATAL, "mylogger", "mythread", new Date());
		TestHelper.addLogs(servlet, 15, Level.DEBUG, "mylogger", "mythread", new Date());
		TestHelper.addLogs(servlet, 10, Level.WARN, "mylogger", "mythread", new Date());
		TestHelper.addLogs(servlet, 10, Level.WARN, "mylogger", "mythread2", new Date());
		TestHelper.addLogs(servlet, 10, Level.WARN, "mylogger", "mythread", new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000));
		
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        stats.doGet(request, response);
        assertEquals("text/csv", response.getContentType());
        
        String[] lines = response.getContentAsString().split("\n");
        String[] header = lines[0].split("\t");
        int pos1 = header[1].equals(format.format(new Date())) ? 1 : 2;
        int pos2 = pos1 == 1 ? 2 : 1;
        for(String line : lines) {
        	String[] cells = line.split("\t");
        	if(cells[0].equals(Level.FATAL.toString())) {
        		assertEquals("7", cells[pos1]);
        	} else if(cells[0].equals(Level.DEBUG.toString())) {
        		assertEquals("15", cells[pos1]);
        	} else if(cells[0].equals(Level.WARN.toString())) {
        		assertEquals("20", cells[pos1]);
        		assertEquals("10", cells[pos2]);
        	} else if(cells[0].equals("mylogger")) {
        		assertEquals("40", cells[pos1]);
        		assertEquals("10", cells[pos2]);
        	} else if(cells[0].equals("mylogger2")) {
        		assertEquals("2", cells[pos1]);
        	} else if(cells[0].equals("mythread")) {
        		assertEquals("32", cells[pos1]);
        		assertEquals("10", cells[pos2]);
        	} else if(cells[0].equals("mythread2")) {
        		assertEquals("10", cells[pos1]);
        	}
        }
	}
}
