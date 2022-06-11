package lk.express.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyUtil {

	/**
	 * Rounds a double value to two decimal places. Uses HALF_UP strategy.
	 * 
	 * @param value
	 *            value to round
	 * @return rounded value
	 * 
	 * @see #round(double, int)
	 */
	public static double round(double value) {
		return round(value, 2);
	}

	/**
	 * Rounds a double value to a specified number of decimal places.
	 * <p>
	 * Uses HALF_UP strategy, i.e. if both neighbors are equidistant, the value
	 * is rounded to the upper value.
	 * 
	 * <p>
	 * Example:
	 * <table border>
	 * <caption><b>Rounding mode HALF_UP Examples</b></caption>
	 * <tr valign=top>
	 * <th>Input Number</th>
	 * <th>Input rounded to two decimal places<br>
	 * with HALF_UP rounding
	 * <tr align=right>
	 * <td>5.125</td>
	 * <td>5.13</td>
	 * <tr align=right>
	 * <td>-5.125</td>
	 * <td>-5.13</td>
	 * </table>
	 * 
	 * 
	 * @param value
	 *            value to round
	 * @param places
	 *            number of decimal places
	 * @return rounded value
	 */
	public static double round(double value, int places) {
		if (places < 0) {
			throw new IllegalArgumentException("Number of decimal places cannot be negative");
		}
		BigDecimal bd = new BigDecimal(String.valueOf(value));
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	/**
	 * Returns whether a value is zero for accounting purposes
	 * 
	 * @param value
	 *            value to asses
	 * @return whether zero
	 */
	public static boolean isZero(double value) {
		value = round(value);
		if (value < 0.01d) {
			return true;
		}
		return false;
	}
}
