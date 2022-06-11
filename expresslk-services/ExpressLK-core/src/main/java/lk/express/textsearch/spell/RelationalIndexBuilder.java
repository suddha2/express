package lk.express.textsearch.spell;

import java.util.List;

import lk.express.bean.Entity;
import lk.express.db.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.criterion.Projections;

public class RelationalIndexBuilder implements IndexBuilder {

	private int k;
	private Class<? extends Entity> clazz;
	private String field;
	private IndexStrategy strategy;

	public RelationalIndexBuilder(int k, Class<? extends Entity> clazz, String field, IndexStrategy strategy) {
		this.k = k;
		this.clazz = clazz;
		this.field = field;
		this.strategy = strategy;
	}

	@Override
	public Index build() {
		Index index = new IndexImpl(k);

		Session session = HibernateUtil.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<String> list = session.createCriteria(clazz)
				.setProjection(Projections.distinct(Projections.property(field))).list();
		strategy.execute(index, list);

		return index;
	}
}
