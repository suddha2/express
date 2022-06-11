package lk.express.admin.service.rest;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lk.express.bean.Entity;
import lk.express.bean.HasPerson;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public abstract class HasPersonService<T extends Entity & HasPerson> extends EntityService<T> {

	protected static final String PERSON_ALIAS = "person";

	public HasPersonService(Class<T> clazz) {
		super(clazz);
	}

	@SuppressWarnings("hiding")
	@Override
	protected <T extends Entity> void populateCriteria(Class<T> clazz, Criteria criteria,
			Map<String, String> pathToAlias, Map<String, List<String>> queryParams) {

		populateCriteria(clazz, criteria, pathToAlias, queryParams, PERSON_ALIAS);
		super.populateCriteria(clazz, criteria, pathToAlias, queryParams);
	}

	@SuppressWarnings("hiding")
	public <T extends Entity> void populateCriteria(Class<T> clazz, Criteria criteria, Map<String, String> pathToAlias,
			Map<String, List<String>> queryParams, String alias) {

		Iterator<Entry<String, List<String>>> iterator = queryParams.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, List<String>> ent = iterator.next();
			String key = ent.getKey();
			if (key.equals("fullName")) {
				String value = getSingleParam(ent.getValue());
				if (NULL.equalsIgnoreCase(value)) {
					value = null;
				}
				if (value != null) {
					String likePredicate = null;
					for (String likeSuffix : LIKE) {
						if (key.endsWith(likeSuffix)) {
							likePredicate = likeSuffix;
							key = key.substring(0, key.length() - likeSuffix.length());
						}
					}
					searchByFullName(value, criteria, likePredicate, alias);
					iterator.remove();
					if (alias != null) {
						pathToAlias.put("person", alias);
					}
				} else {
					criteria.add(Restrictions.isNull(key));
				}
			}
		}
	}

	protected void searchByFullName(String value, Criteria criteria, String likePredicate, String alias) {

		String aliasDot = "";
		if (alias != null) {
			aliasDot = alias + ".";
		}

		String[] nameParts = value.split("\\s+");
		if (nameParts.length == 1) {
			criteria.add(Restrictions.or(
					getCriteria(String.class, aliasDot + "firstName", nameParts[0], likePredicate),
					getCriteria(String.class, aliasDot + "lastName", nameParts[0], likePredicate),
					getCriteria(String.class, aliasDot + "nic", nameParts[0], likePredicate)));

		} else if (nameParts.length == 2) {
			criteria.add(getCriteria(String.class, aliasDot + "firstName", nameParts[0], likePredicate));
			criteria.add(getCriteria(String.class, aliasDot + "lastName", nameParts[1], likePredicate));
		} else if (nameParts.length > 2) {
			criteria.add(getCriteria(String.class, aliasDot + "firstName", nameParts[0], likePredicate));
			criteria.add(getCriteria(String.class, aliasDot + "lastName", nameParts[1], likePredicate));
			String nic = nameParts[2].replace("(", "").replace(")", "");
			criteria.add(getCriteria(String.class, aliasDot + "nic", nic, likePredicate));
		}
	}

	@SuppressWarnings("hiding")
	@Override
	protected <T extends Entity> void addSortAndLimitToCriteria(Class<T> clazz, Criteria criteria,
			Map<String, String> pathToAlias, Map<String, List<String>> queryParams) {

		addSortAndLimitToCriteria(clazz, criteria, pathToAlias, queryParams, PERSON_ALIAS);
		super.addSortAndLimitToCriteria(clazz, criteria, pathToAlias, queryParams);
	}

	@SuppressWarnings("hiding")
	public <T extends Entity> void addSortAndLimitToCriteria(Class<T> clazz, Criteria criteria,
			Map<String, String> pathToAlias, Map<String, List<String>> queryParams, String alias) {

		String aliasDot = "";
		if (alias != null) {
			aliasDot = alias + ".";
		}

		String sortField = getSingleParam(queryParams.get(SORT_FIELD));
		if (sortField != null && sortField.equals("fullName")) {

			String sortOrderDirection = getSortOrderDir(queryParams);
			if (SORT_DIR_DESC.equals(sortOrderDirection)) {
				criteria.addOrder(Order.desc(aliasDot + "firstName"));
				criteria.addOrder(Order.desc(aliasDot + "lastName"));
				criteria.addOrder(Order.desc(aliasDot + "nic"));
			} else {
				criteria.addOrder(Order.asc(aliasDot + "firstName"));
				criteria.addOrder(Order.asc(aliasDot + "lastName"));
				criteria.addOrder(Order.asc(aliasDot + "nic"));
			}
			if (alias != null) {
				pathToAlias.put("person", alias);
			}
		}
	}
}
