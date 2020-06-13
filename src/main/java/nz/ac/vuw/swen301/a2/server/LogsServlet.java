/**
 * 
 */
package nz.ac.vuw.swen301.a2.server;

import java.util.Map;
import java.util.function.Predicate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Claire
 *
 */
public class LogsServlet extends HttpServlet {

	private static final long serialVersionUID = 3441175578112383791L;

	/**
	 * 
	 */
	public LogsServlet() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	{
		resp.setContentType("application/json");
		Map<String, String[]> params = req.getParameterMap();
		
		
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	{
		
	}
	
	private static int getInt32(Map<String, String[]> params, Predicate<Integer> acceptanceCriteria) {
		return 0;
	}
	
	private static void fail(HttpServletResponse resp, String reason) {
		
	}

}
