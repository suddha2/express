package lk.express.rule;

import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.easymock.IMockBuilder;
import org.junit.Before;
import org.junit.Test;

public class CombinerTest {

	private ICondition<Object> trueCondition;
	private ICondition<Object> falseCondition;
	private Object obj = new Object();

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		@SuppressWarnings("rawtypes")
		IMockBuilder<Condition> builder = createMockBuilder(Condition.class);
		builder.addMockedMethod("matches");

		trueCondition = builder.createMock();
		expect(trueCondition.matches(obj)).andReturn(true).anyTimes();
		replay(trueCondition);

		falseCondition = builder.createMock();
		expect(falseCondition.matches(obj)).andReturn(false).anyTimes();
		replay(falseCondition);
	}

	/**
	 * Test method for
	 * {@link lk.express.rule.Combiner#bySymbol(java.lang.String)}.
	 */
	@Test
	public void testBySymbol() {
		assertEquals(Combiner.And, Combiner.bySymbol("&"));
		assertEquals(Combiner.Or, Combiner.bySymbol("|"));
	}

	/**
	 * Test method for
	 * {@link lk.express.rule.Combiner#combine(lk.express.rule.ICondition, lk.express.rule.ICondition, java.lang.Object)}
	 * .
	 */
	@Test
	public void testCombineAnd() {
		assertTrue(Combiner.And.combine(trueCondition, trueCondition, obj));
		assertFalse(Combiner.And.combine(trueCondition, falseCondition, obj));
		assertFalse(Combiner.And.combine(falseCondition, trueCondition, obj));
		assertFalse(Combiner.And.combine(falseCondition, falseCondition, obj));
	}

	/**
	 * Test method for
	 * {@link lk.express.rule.Combiner#combine(lk.express.rule.ICondition, lk.express.rule.ICondition, java.lang.Object)}
	 * .
	 */
	@Test
	public void testCombineOr() {
		assertTrue(Combiner.Or.combine(trueCondition, trueCondition, obj));
		assertTrue(Combiner.Or.combine(trueCondition, falseCondition, obj));
		assertTrue(Combiner.Or.combine(falseCondition, trueCondition, obj));
		assertFalse(Combiner.Or.combine(falseCondition, falseCondition, obj));
	}
}
