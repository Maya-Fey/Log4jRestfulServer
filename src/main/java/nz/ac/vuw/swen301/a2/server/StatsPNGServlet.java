/**
 * 
 */
package nz.ac.vuw.swen301.a2.server;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.json.JSONObject;

/**
 * @author Claire
 */
public class StatsPNGServlet extends HttpServlet {

	private static final long serialVersionUID = -1562859421966171534L;
	
	private static final int BORDER_SIZE = 2;
	private static final int PADDING_SIZE = 25;
	private static final int CHART_HEIGHT = 100;
	private static final int BAR_WIDTH = 40;
	private static final int BAR_MARGIN = 25;
	private static final int WIDTH = 500;

	/**
	 * @param req
	 * @param resp
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	{
		resp.setContentType("image/png");
		resp.setHeader("content-disposition", "inline; filename=\"stats.png\"");
		
		try(ServletOutputStream out = resp.getOutputStream()) {
			BufferedImage image = new BufferedImage(WIDTH, BORDER_SIZE * 2 + PADDING_SIZE * 2 + CHART_HEIGHT, BufferedImage.TYPE_INT_RGB);
	        Graphics2D g = image.createGraphics();
	        
	        Map<Level, Integer> counts = new HashMap<>();
	        counts.put(Level.DEBUG, 0);
	        counts.put(Level.TRACE, 0);
	        counts.put(Level.INFO, 0);
	        counts.put(Level.WARN, 0);
	        counts.put(Level.ERROR, 0);
	        counts.put(Level.FATAL, 0);
	        
	        LogsServlet.list.getLogs(Level.ALL, Integer.MAX_VALUE).forEach((o) -> {
	        	JSONObject obj = (JSONObject) o;
	        	Level level = Level.toLevel(obj.getString("level"));
	        	counts.put(level, counts.get(level) + 1);
	        });
	        
	        int max = 0;
	        for(int i : counts.values())
	        	if(i > max)
	        		max = i;
	        
	        g.setColor(Color.BLACK);
	        g.fillRect(0, 0, 700, BORDER_SIZE * 2 + PADDING_SIZE * 2 + CHART_HEIGHT);
	        g.setColor(Color.WHITE);
	        g.fillRect(BORDER_SIZE, BORDER_SIZE, WIDTH - BORDER_SIZE * 2, PADDING_SIZE * 2 + CHART_HEIGHT);
	        g.setColor(Color.BLACK);
	        g.setStroke(new BasicStroke());
	        g.drawString("" + max, BORDER_SIZE + PADDING_SIZE, BORDER_SIZE + PADDING_SIZE + 5);
	        g.drawLine(BORDER_SIZE + PADDING_SIZE + BAR_WIDTH, BORDER_SIZE + PADDING_SIZE, BORDER_SIZE + PADDING_SIZE + BAR_WIDTH * 2, BORDER_SIZE + PADDING_SIZE);
	        g.drawString("0", BORDER_SIZE + PADDING_SIZE, BORDER_SIZE + PADDING_SIZE + 5 + CHART_HEIGHT);
	        g.drawLine(BORDER_SIZE + PADDING_SIZE + BAR_WIDTH, BORDER_SIZE + PADDING_SIZE + CHART_HEIGHT, BORDER_SIZE + PADDING_SIZE + BAR_WIDTH * 2, BORDER_SIZE + PADDING_SIZE + CHART_HEIGHT);
	        g.drawLine(BORDER_SIZE + PADDING_SIZE + BAR_WIDTH + BAR_WIDTH / 2, BORDER_SIZE + PADDING_SIZE, BORDER_SIZE + PADDING_SIZE + BAR_WIDTH + BAR_WIDTH / 2, BORDER_SIZE + PADDING_SIZE + CHART_HEIGHT);
	        
	        int xpos = BORDER_SIZE + PADDING_SIZE + BAR_WIDTH + BAR_WIDTH / 2 + BAR_MARGIN;
	        for(Map.Entry<Level, Integer> entry : counts.entrySet()) {
	        	int height = (100 * entry.getValue()) / max;
	        	g.fillRect(xpos, BORDER_SIZE + PADDING_SIZE + CHART_HEIGHT - height, BAR_WIDTH, height);
	        	g.drawString(entry.getKey().toString(), xpos, BORDER_SIZE + PADDING_SIZE + CHART_HEIGHT + 12);
	        	xpos += BAR_WIDTH + BAR_MARGIN;
	        }
	        
	        ImageIO.write(image, "png", out);
	        
	        g.dispose();      
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		resp.setStatus(HttpServletResponse.SC_OK);
	}

}
