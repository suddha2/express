package lk.express.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class QualifierTest {

	/**
	 * Test method for
	 * {@link lk.express.rule.Qualifier#bySymbol(java.lang.String)}.
	 */
	@Test
	public void testBySymbol() {
		assertEquals(Qualifier.Equals, Qualifier.bySymbol("="));
		assertEquals(Qualifier.GreaterThan, Qualifier.bySymbol(">"));
		assertEquals(Qualifier.GreaterThanOrEquals, Qualifier.bySymbol(">="));
		assertEquals(Qualifier.LessThan, Qualifier.bySymbol("<"));
		assertEquals(Qualifier.LessThanOrEquals, Qualifier.bySymbol("<="));
		assertEquals(Qualifier.Like, Qualifier.bySymbol("LIKE"));
		assertEquals(Qualifier.NotEquals, Qualifier.bySymbol("!="));
	}

	/**
	 * Test method for
	 * {@link lk.express.rule.Qualifier#evaluate(java.lang.Object, java.lang.Object)}
	 * .
	 */
	@Test
	public void testEvaluateEquals() {
		assertTrue(Qualifier.Equals.evaluate(2, 2));
		assertTrue(Qualifier.Equals.evaluate(2.0123, 2.0123));
		assertTrue(Qualifier.Equals.evaluate(2L, 2L));
		assertFalse(Qualifier.Equals.evaluate(2, 3));

		Object obj = new Object();
		assertTrue(Qualifier.Equals.evaluate(obj, obj));
		assertFalse(Qualifier.Equals.evaluate(obj, new Object()));
		assertTrue(Qualifier.Equals.evaluate("A", "A"));
		assertFalse(Qualifier.Equals.evaluate("A", "B"));
	}

	/**
	 * Test method for
	 * {@link lk.express.rule.Qualifier#evaluate(java.lang.Object, java.lang.Object)}
	 * .
	 */
	@Test
	public void testEvaluateNotEquals() {
		assertFalse(Qualifier.NotEquals.evaluate(2, 2));
		assertTrue(Qualifier.NotEquals.evaluate("A", "B"));
	}

	/**
	 * Test method for
	 * {@link lk.express.rule.Qualifier#evaluate(java.lang.Object, java.lang.Object)}
	 * .
	 */
	@Test
	public void testEvaluateLessThanOrEquals() {
		assertTrue(Qualifier.LessThanOrEquals.evaluate(1, 2));
		assertTrue(Qualifier.LessThanOrEquals.evaluate(1.0, 2.0));
		assertTrue(Qualifier.LessThanOrEquals.evaluate(1L, 2L));

		assertTrue(Qualifier.LessThanOrEquals.evaluate(2, 2));
		assertTrue(Qualifier.LessThanOrEquals.evaluate(2.0123, 2.0123));
		assertTrue(Qualifier.LessThanOrEquals.evaluate(2L, 2L));

		assertFalse(Qualifier.LessThanOrEquals.evaluate("A", "B"));
	}

	/**
	 * Test method for
	 * {@link lk.express.rule.Qualifier#evaluate(java.lang.Object, java.lang.Object)}
	 * .
	 */
	@Test
	public void testEvaluateLike() {
		assertTrue(Qualifier.Like.evaluate("Abc", "Abc"));
		assertTrue(Qualifier.Like.evaluate("Abc", "abC"));
		assertFalse(Qualifier.Like.evaluate("A", "B"));

		assertFalse(Qualifier.Like.evaluate(1, 1));
		Object obj = new Object();
		assertFalse(Qualifier.Like.evaluate(obj, obj));
	}
}
