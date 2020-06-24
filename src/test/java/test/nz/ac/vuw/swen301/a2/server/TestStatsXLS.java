/**
 * 
 */
package test.nz.ac.vuw.swen301.a2.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import org.apache.log4j.Level;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import nz.ac.vuw.swen301.a2.server.LogsServlet;
import nz.ac.vuw.swen301.a2.server.StatsXLSServlet;

/**
 * @author Claire
 */
public class TestStatsXLS {
	
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
		StatsXLSServlet stats = new StatsXLSServlet();
		TestHelper.addLogs(servlet, 2, Level.FATAL, "mylogger2", "mythread", new Date());
		TestHelper.addLogs(servlet, 5, Level.FATAL, "mylogger", "mythread", new Date());
		TestHelper.addLogs(servlet, 15, Level.DEBUG, "mylogger", "mythread", new Date());
		TestHelper.addLogs(servlet, 10, Level.WARN, "mylogger", "mythread", new Date());
		TestHelper.addLogs(servlet, 10, Level.WARN, "mylogger", "mythread2", new Date());
		TestHelper.addLogs(servlet, 10, Level.WARN, "mylogger", "mythread", new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000));
		
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        stats.doGet(request, response);
        assertEquals("application/vnd.ms-excel", response.getContentType());
        
        Workbook workbook = new HSSFWorkbook(new ByteArrayInputStream(response.getContentAsByteArray()));
        Sheet sheet = workbook.getSheet("status");
        
        int pos1 = sheet.getRow(0).getCell(1).getStringCellValue().equals(format.format(new Date())) ? 1 : 2;
        int pos2 = pos1 == 1 ? 2 : 1;
        Iterator<Row> bodyiterator = sheet.rowIterator();
        while(bodyiterator.hasNext()) {
        	Row row = bodyiterator.next();
        	String cellname = row.getCell(0).getStringCellValue();
        	if(cellname.equals(""))
        		continue;
        	String cellpos1 = "" + ((int) row.getCell(pos1).getNumericCellValue());
        	String cellpos2 = "" + ((int) row.getCell(pos2).getNumericCellValue());
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
        workbook.close();
	}
}
