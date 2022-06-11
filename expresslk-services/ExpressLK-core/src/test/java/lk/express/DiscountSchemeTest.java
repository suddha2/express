package lk.express;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lk.express.bean.City;
import lk.express.rule.ApplicationType;
import lk.express.rule.Combiner;
import lk.express.rule.Condition;
import lk.express.rule.ConditionGroup;
import lk.express.rule.ICondition;
import lk.express.rule.IRule;
import lk.express.rule.Qualifier;
import lk.express.rule.Scheme;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DiscountSchemeTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testApply() {

		ResultSector subject = new ResultSector();
		subject.setCost(500);
		ResultWrapper wrapper = new ResultWrapper(null, subject);

		City galle = new City();
		galle.setId(0);
		galle.setCode("GAL");
		galle.setName("Galle");
		subject.setFromCity(galle);

		City matara = new City();
		matara.setId(1);
		matara.setCode("MAT");
		matara.setName("Matara");
		subject.setToCity(matara);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -10);
		Date dep = cal.getTime();
		subject.setDepartureTime(dep);

		cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -8);
		Date arr = cal.getTime();
		subject.setArrivalTime(arr);

		ICondition<ResultWrapper> condition00 = new Condition<ResultWrapper>("fromCity", Qualifier.Equals, galle);
		ICondition<ResultWrapper> condition01 = new Condition<ResultWrapper>("toCity", Qualifier.Equals, matara);
		ICondition<ResultWrapper> condition0 = new ConditionGroup<ResultWrapper>(condition00, condition01, Combiner.And);
		DiscountRule rule0 = new DiscountRule(0, "zeroRule", condition0, 0.5f, "discount");
		rule0.setAmount(0.25f);
		rule0.setApplicationType(ApplicationType.Percentage);

		ICondition<ResultWrapper> condition100 = new Condition<ResultWrapper>("departureTime",
				Qualifier.BeforeDateTime, new Date());
		ICondition<ResultWrapper> condition101 = new Condition<ResultWrapper>("arrivalTime", Qualifier.BeforeDateTime,
				new Date());
		ICondition<ResultWrapper> condition10 = new ConditionGroup<ResultWrapper>(condition100, condition101,
				Combiner.And);

		ICondition<ResultWrapper> condition110 = new Condition<ResultWrapper>("arrivalTime", Qualifier.AfterDateTime,
				new Date());
		ICondition<ResultWrapper> condition11 = new ConditionGroup<ResultWrapper>(condition10, condition110,
				Combiner.Or);
		DiscountRule rule1 = new DiscountRule(1, "oneRule", condition11, 0.4f, "discount");
		rule1.setAmount(100);
		rule1.setApplicationType(ApplicationType.Absolute);

		List<IRule<ResultWrapper>> rules = new ArrayList<IRule<ResultWrapper>>();
		rules.add(rule0);
		rules.add(rule1);
		Scheme<ResultWrapper> discountScheme = new Scheme<ResultWrapper>("discount", rules);
		discountScheme.setApplySingle(false);

		discountScheme.apply(wrapper);
	}
}
