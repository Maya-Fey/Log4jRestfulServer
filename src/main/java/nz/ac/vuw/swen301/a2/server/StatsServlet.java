/**
 * 
 */
package nz.ac.vuw.swen301.a2.server;

import java.io.IOException;
import java.io.UncheckedIOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;

/**
 * @author Claire
 *
 */
public class StatsServlet extends HttpServlet {

	private static final long serialVersionUID = -8593699354286628485L;

	/**
	 * @param req
	 * @param resp
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	{
		resp.setContentType("text/html");
		
		try {
			resp.getWriter().append(new LogIndex(LogsServlet.list.getLogs(Level.ALL, Integer.MAX_VALUE)).toHTMLTable());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	
}
