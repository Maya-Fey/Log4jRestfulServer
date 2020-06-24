/**
 * 
 */
package test.nz.ac.vuw.swen301.a2.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Date;

import org.junit.jupiter.api.Test;

import nz.ac.vuw.swen301.a2.server.LogIndex;

/**
 * Testing miscellaneous classes
 * 
 * @author Claire
 */
public class TestMisc {
	
	/**
	 * Tests the equality between two timestamp-removed date objects
	 */
	@Test
	public void testDateEquality()
	{
		long time = System.currentTimeMillis();
		Date date1 = new Date(time);
		Date date2 = new Date(time);
		assertFalse(date1 == date2);
		assertEquals(date1, date2);
		assertEquals(LogIndex.removeTime(date1), LogIndex.removeTime(date2));
	}

}
