/**
 * 
 */
package test.nz.ac.vuw.swen301.a2.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import org.apache.log4j.Level;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import nz.ac.vuw.swen301.a2.server.LogsServlet;
import nz.ac.vuw.swen301.a2.server.StatsServlet;

/**
 * @author Claire
 */
public class TestStatsHTML {
	
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
		StatsServlet stats = new StatsServlet();
		TestHelper.addLogs(servlet, 2, Level.FATAL, "mylogger2", "mythread", new Date());
		TestHelper.addLogs(servlet, 5, Level.FATAL, "mylogger", "mythread", new Date());
		TestHelper.addLogs(servlet, 15, Level.DEBUG, "mylogger", "mythread", new Date());
		TestHelper.addLogs(servlet, 10, Level.WARN, "mylogger", "mythread", new Date());
		TestHelper.addLogs(servlet, 10, Level.WARN, "mylogger", "mythread2", new Date());
		TestHelper.addLogs(servlet, 10, Level.WARN, "mylogger", "mythread", new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000));
		
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        stats.doGet(request, response);
        assertEquals("text/html", response.getContentType());
        
        Document doc = Jsoup.parseBodyFragment(response.getContentAsString());
        Element table = doc.body().child(0);
        Elements body = table.getElementsByTag("tbody").get(0).getElementsByTag("tr");
        Elements header = table.getElementsByTag("thead").get(0).getElementsByTag("tr").get(0).getElementsByTag("td");
        
        int pos1 = header.get(1).childNode(0).toString().trim().equals(format.format(new Date())) ? 1 : 2;
        int pos2 = pos1 == 1 ? 2 : 1;
        Iterator<Element> bodyiterator = body.iterator();
        while(bodyiterator.hasNext()) {
        	Element row = bodyiterator.next();
        	Elements cells = row.getElementsByTag("td");
        	String cellname = cells.get(0).childNode(0).toString().trim();
        	String cellpos1 = cells.get(pos1).childNode(0).toString().trim();
        	String cellpos2 = cells.get(pos2).childNode(0).toString().trim();
        	if(cellname.equals(Level.FATAL.toString())) {
        		assertEquals("7", cellpos1);
        	} else if(cellname.equals(Level.DEBUG.toString())) {
        		assertEquals("15", cellpos1);
        	} else if(cellname.equals(Level.WARN.toString())) {
        		assertEquals("20", cellpos1);
        		assertEquals("10", cellpos2);
        	} else if(cellname.equals("mylogger")) {
        		assertEquals("40", cellpos1);
        		assertEquals("10", cellpos2);
        	} else if(cellname.equals("mylogger2")) {
        		assertEquals("2", cellpos1);
        	} else if(cellname.equals("mythread")) {
        		assertEquals("32", cellpos1);
        		assertEquals("10", cellpos2);
        	} else if(cellname.equals("mythread2")) {
        		assertEquals("10", cellpos1);
        	}
        }
	}
}
