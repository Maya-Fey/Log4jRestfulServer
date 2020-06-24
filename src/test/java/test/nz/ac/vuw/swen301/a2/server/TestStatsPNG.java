/**
 * 
 */
package test.nz.ac.vuw.swen301.a2.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import nz.ac.vuw.swen301.a2.server.LogsServlet;
import nz.ac.vuw.swen301.a2.server.StatsPNGServlet;

/**
 * @author Claire
 */
public class TestStatsPNG {
	
	/**
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testPNG() throws IOException
	{
		LogsServlet servlet = new LogsServlet();
		StatsPNGServlet stats = new StatsPNGServlet();
		TestHelper.addLogs(servlet, 5, Level.FATAL, "mylogger", "mythread", new Date());
		TestHelper.addLogs(servlet, 15, Level.DEBUG, "mylogger", "mythread", new Date());
		TestHelper.addLogs(servlet, 10, Level.WARN, "mylogger", "mythread", new Date());
		
		MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        stats.doGet(request, response);
        ImageIO.read(new ByteArrayInputStream(response.getContentAsByteArray()));
        assertEquals("image/png", response.getContentType());
	}

}
