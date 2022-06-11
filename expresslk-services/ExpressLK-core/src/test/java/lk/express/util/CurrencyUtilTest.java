package lk.express.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CurrencyUtilTest {

	private static final double delta = 0.000000001;

	/**
	 * Test method for {@link lk.express.util.CurrencyUtil#round(double)}.
	 */
	@Test
	public void testRoundDouble() {
		// up
		assertEquals(12.35, CurrencyUtil.round(12.346), delta);

		// down
		assertEquals(-12.34, CurrencyUtil.round(-12.341), delta);
	}

	/**
	 * Test method for {@link lk.express.util.CurrencyUtil#round(double, int)}.
	 */
	@Test
	public void testRoundDoubleInt() {
		// positive down
		assertEquals(12.3, CurrencyUtil.round(12.346, 1), delta);

		// positive up
		assertEquals(12.4, CurrencyUtil.round(12.364, 1), delta);

		// negative down
		assertEquals(-12.3, CurrencyUtil.round(-12.346, 1), delta);

		// negative up
		assertEquals(-12.4, CurrencyUtil.round(-12.364, 1), delta);

		// half up in positive
		assertEquals(12.4, CurrencyUtil.round(12.35, 1), delta);

		// half up in negative
		assertEquals(-12.4, CurrencyUtil.round(-12.35, 1), delta);

		// large number of decimal places
		assertEquals(-12.1235, CurrencyUtil.round(-12.123456, 4), delta);

		// 0 decimal places
		assertEquals(12, CurrencyUtil.round(12.35, 0), delta);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoundWithNegativeDecimalPlaces() {
		// negative decimal places
		CurrencyUtil.round(12.35, -1);
	}
}
