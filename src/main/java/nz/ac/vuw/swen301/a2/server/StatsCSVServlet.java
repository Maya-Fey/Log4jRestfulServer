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
 */
public class StatsCSVServlet extends HttpServlet {

	private static final long serialVersionUID = -2229632319048970200L;

	/**
	 * @param req
	 * @param resp
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	{
		resp.setContentType("text/csv");
		
		resp.setHeader("content-disposition", "inline; filename=\"stats.csv\"");
		try {
			resp.getWriter().append(new LogIndex(LogsServlet.list.getLogs(Level.ALL, Integer.MAX_VALUE)).toCSV());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		
		resp.setStatus(HttpServletResponse.SC_OK);
	}

}
