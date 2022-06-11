package lk.express.service;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED;
import static javax.ws.rs.core.Response.Status.OK;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.core.Response;

import lk.express.Context;
import lk.express.bean.BelongsToDivision;
import lk.express.bean.Entity;
import lk.express.bean.HasAllowedDivisions;
import lk.express.bean.HasNameCode;
import lk.express.db.HibernateUtil;
import lk.express.db.dao.GenericDAO;
import lk.express.db.dao.HasCodeDAO;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RestService extends AbstractService {

	public static final String PAGE_START = "pageStart";
	public static final String PAGE_ROWS = "pageRows";
	public static final String SORT_FIELD = "sortField";
	public static final String SORT_DIR = "sortDir";
	public static final String SORT_DIR_ASC = "asc";
	public static final String SORT_DIR_DESC = "desc";

	public static final String NULL = "null";
	public static final String LIKE_EXACT = "~";
	public static final String LIKE_START = "^";
	public static final String LIKE_END = "$";
	public static final String LIKE_ANYWHERE = "*";
	protected static final String[] LIKE = new String[] { LIKE_EXACT, LIKE_START, LIKE_END, LIKE_ANYWHERE };

	public static final int DEFAULT_PAGE_ROWS = 10;

	protected static final String MSG_INVALID_ID = "Invalid ID!";
	protected static final String MSG_INVALID_CODE = "Invalid code!";
	protected static final String MSG_INTERNAL_SERVER_ERROR = "Internal server error occurred!";
	protected static final String MSG_CONSTRINT_VIOLATED_ON_PUT = "Unable to insert/update due to database contraint violations!";
	protected static final String MSG_CONSTRINT_VIOLATED_ON_DELETE = "There seems to be other objects"
			+ " referring to the oject you are trying to delete, preventing you from deleting the object!";

	private static final Logger logger = LoggerFactory.getLogger(RestService.class);

	public static final class RestResponse {

		private Object result;

		public RestResponse() {
		}

		public RestResponse(Object result) {
			this.result = result.toString();
		}

		public Object getResult() {
			return result;
		}

		public void setResult(Object result) {
			this.result = result;
		}
	}

	public static final class RestErrorResponse {

		private String error;

		public RestErrorResponse() {
		}

		public RestErrorResponse(String error) {
			this.error = error;
		}

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}
	}

	public <T extends Entity> Response getEntity(Class<T> clazz, String idString) {
		Integer id = null;
		try {
			id = Integer.valueOf(idString);
		} catch (NumberFormatException nfe) {
			return Response.status(BAD_REQUEST).entity(new RestErrorResponse(MSG_INVALID_ID)).build();
		}
		try {
			T t = load(clazz, id);
			Hibernate.initialize(t);
			if (t == null) {
				return Response.status(BAD_REQUEST).entity(new RestErrorResponse(MSG_INVALID_ID)).build();
			}
			return Response.status(OK).entity(t).build();
		} catch (Exception e) {
			logger.error("Error while retrieving entity", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
					.build();
		}
	}

	public <T extends Entity & HasNameCode> Response getEntityByCode(Class<T> clazz, String code) {
		try {
			T t = load(clazz, code);
			Hibernate.initialize(t);
			if (t == null) {
				return Response.status(BAD_REQUEST).entity(new RestErrorResponse(MSG_INVALID_CODE)).build();
			}
			return Response.status(OK).entity(t).build();
		} catch (Exception e) {
			logger.error("Error while retrieving entity", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
					.build();
		}
	}

	public <T extends Entity> Response getEntities(Class<T> clazz) {
		try {
			List<T> t = list(clazz);
			for (T a : t) {
				Hibernate.initialize(a);
			}
			return Response.status(OK).header("X-Total-Count", t.size()).entity(t).build();
		} catch (Exception e) {
			logger.error("Error while retrieving entities", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
					.build();
		}
	}

	public <T extends Entity> Response getEntities(Class<T> clazz, Map<String, List<String>> queryParams) {
		try {
			Criteria criteria = HibernateUtil.createCriteria(clazz);

			Map<String, String> pathToAlias = new HashMap<String, String>();
			populateCriteria(clazz, criteria, pathToAlias, queryParams);
			for (Entry<String, String> e : pathToAlias.entrySet()) {
				criteria.createAlias(e.getKey(), e.getValue());
			}

			Long count = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
			criteria.setProjection(null);
			criteria.setResultTransformer(Criteria.ROOT_ENTITY);

			Map<String, String> moreAlias = new HashMap<String, String>();
			addSortAndLimitToCriteria(clazz, criteria, moreAlias, queryParams);
			// Remove already added aliases
			for (String key : pathToAlias.keySet()) {
				moreAlias.remove(key);
			}
			for (Entry<String, String> e : moreAlias.entrySet()) {
				criteria.createAlias(e.getKey(), e.getValue());
			}

			@SuppressWarnings("unchecked")
			List<T> t = criteria.list();
			for (T a : t) {
				Hibernate.initialize(a);
			}
			return Response.status(OK).header("X-Total-Count", count).entity(t).build();
		} catch (Exception e) {
			logger.error("Error while retrieving entities", e);
			HibernateUtil.rollback();
			return Response.status(BAD_REQUEST).build();
		}
	}

	protected <T extends Entity> void populateCriteria(Class<T> clazz, Criteria criteria,
			Map<String, String> pathToAlias, Map<String, List<String>> queryParams) {

		for (Entry<String, List<String>> entity : queryParams.entrySet()) {
			String key = entity.getKey();

			// skip pagination and sorting parameters
			if (key.equals(PAGE_START) || key.equals(PAGE_ROWS) || key.equals(SORT_FIELD) || key.equals(SORT_DIR)) {
				continue;
			}

			String likePredicate = null;
			for (String likeSuffix : LIKE) {
				if (key.endsWith(likeSuffix)) {
					likePredicate = likeSuffix;
					key = key.substring(0, key.length() - likeSuffix.length());
				}
			}

			String propertyName = addAliasAndGetPropertyName(key, pathToAlias);

			List<String> values = entity.getValue();
			String singleValue = getSingleParam(entity.getValue());
			if (NULL.equalsIgnoreCase(singleValue)) {
				values = null;
			}
			if (values != null) {
				try {
					Field field = getField(clazz, key);
					field.setAccessible(true);
					Class<?> fieldClazz = field.getType();

					Criterion oneCriterion = null;
					for (String value : values) {
						Object o = populateObject(fieldClazz, value);
						Criterion c = getCriteria(fieldClazz, propertyName, o, likePredicate);
						oneCriterion = oneCriterion == null ? c : Restrictions.or(oneCriterion, c);
					}
					if (oneCriterion != null) {
						criteria.add(oneCriterion);
					}
				} catch (NoSuchFieldException | NoSuchMethodException e) {
				} catch (Exception e) {
					logger.error("", e);
				}
			} else {
				criteria.add(Restrictions.isNull(propertyName));
			}
		}
	}

	protected String addAliasAndGetPropertyName(String field, Map<String, String> pathToAlias) {
		String[] parts = field.split("\\.|_");
		String propertyName = null;
		if (parts.length == 1) {
			propertyName = field;
		} else {
			String path = "", alias = "";
			for (int a = 0; a < parts.length - 1; a++) {
				path = alias + (a != 0 ? "." : "") + parts[a];
				alias = alias + (a != 0 ? "_" : "") + parts[a];
				pathToAlias.put(path, alias);
			}
			propertyName = alias + "." + parts[parts.length - 1];
		}
		return propertyName;
	}

	protected Criterion getCriteria(Class<?> fieldClazz, String key, Object o, String likePredicate) {
		if (fieldClazz.equals(String.class) && LIKE_EXACT.equals(likePredicate)) {
			return Restrictions.ilike(key, o.toString(), MatchMode.EXACT);
		} else if (fieldClazz.equals(String.class) && LIKE_START.equals(likePredicate)) {
			return Restrictions.ilike(key, o.toString(), MatchMode.START);
		} else if (fieldClazz.equals(String.class) && LIKE_END.equals(likePredicate)) {
			return Restrictions.ilike(key, o.toString(), MatchMode.END);
		} else if (fieldClazz.equals(String.class) && LIKE_ANYWHERE.equals(likePredicate)) {
			return Restrictions.ilike(key, o.toString(), MatchMode.ANYWHERE);
		} else {
			return Restrictions.eq(key, o);
		}
	}

	protected String getSingleParam(List<String> list) {
		if (list != null && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	protected <T extends Entity> void addSortAndLimitToCriteria(Class<T> clazz, Criteria criteria,
			Map<String, String> pathToAlias, Map<String, List<String>> queryParams) {

		String pageStart = getSingleParam(queryParams.get(PAGE_START));
		if (pageStart != null) {
			int start = Integer.valueOf(pageStart);
			if (start >= 0) {
				criteria.setFirstResult(start);
			}
		}

		String pageRows = getSingleParam(queryParams.get(PAGE_ROWS));
		if (pageRows != null) {
			int rows = Integer.valueOf(pageRows);
			if (rows > 0) {
				criteria.setMaxResults(rows);
			}
		} else {
			criteria.setMaxResults(DEFAULT_PAGE_ROWS);
		}

		String sortField = getSingleParam(queryParams.get(SORT_FIELD));
		if (sortField != null) {
			try {
				String propertyName = addAliasAndGetPropertyName(sortField, pathToAlias);

				Field field = getField(clazz, sortField);
				// if there is actually such field
				if (field != null) {
					String sortOrderDirection = getSortOrderDir(queryParams);
					Order o = null;
					if (SORT_DIR_DESC.equals(sortOrderDirection)) {
						o = Order.desc(propertyName);
					} else {
						o = Order.asc(propertyName);
					}
					criteria.addOrder(o);
				}
			} catch (NoSuchFieldException e) {
			}
		}
	}

	protected String getSortOrderDir(Map<String, List<String>> queryParams) {
		String sortOrderDirection = SORT_DIR_ASC;
		String sortDirection = getSingleParam(queryParams.get(SORT_DIR));
		if (sortDirection != null && SORT_DIR_DESC.equalsIgnoreCase(sortDirection)) {
			sortOrderDirection = SORT_DIR_DESC;
		}

		return sortOrderDirection;
	}

	protected Object populateObject(Class<?> clazz, String value) throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		if (Entity.class.isAssignableFrom(clazz)) {
			@SuppressWarnings("unchecked")
			GenericDAO<Entity> dao = getDAO((Class<Entity>) clazz);
			Integer id = Integer.valueOf(value);
			return dao.get(id);

		} else if (clazz.equals(String.class)) {
			return value;

		} else {
			Constructor<?> constructor = clazz.getConstructor(String.class);
			constructor.setAccessible(true);
			return constructor.newInstance(value);
		}
	}

	protected static Field getField(Class<?> startClass, String path) throws NoSuchFieldException, SecurityException {
		String[] parts = path.split("\\.|_");
		Field field = null;
		for (String part : parts) {
			field = getFieldUp(startClass, part);
			field.setAccessible(true);
			startClass = field.getType();
		}
		return field;
	}

	protected static Field getFieldUp(Class<?> startClass, String name) throws NoSuchFieldException, SecurityException {
		Field field = null;
		try {
			field = startClass.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
		}
		if (field == null) {
			Class<?> parentClass = startClass.getSuperclass();
			if (parentClass != null && !(parentClass.equals(Object.class))) {
				return getFieldUp(parentClass, name);
			} else {
				throw new NoSuchFieldException();
			}
		} else {
			return field;
		}
	}

	@SuppressWarnings("unchecked")
	protected <T extends Entity> Response insertEntity(T t) {
		logger.info("Adding entity. class: {}, entity: {}", new Object[] { t.getClass().getCanonicalName(), t });
		try {
			populateEntity(t);
			Session session = HibernateUtil.getCurrentSession();
			t = (T) session.merge(t);
			return Response.status(OK).entity(t).build();
		} catch (ConstraintViolationException e) {
			return handleConstraintViolationExceptionOnPut(e, t);
		} catch (Exception e) {
			logger.error("Error while putting entity", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
					.build();
		}
	}

	@SuppressWarnings("unchecked")
	protected <T extends Entity> Response updateEntity(T t) {
		logger.info("Updating  entity. class: {}, entity: {}", new Object[] { t.getClass().getCanonicalName(), t });
		try {
			T oldT = (T) load(t.getClass(), t.getId());
			if (oldT == null) {
				logger.info("Entity not found for update. class: {}, id: {}", new Object[] {
						t.getClass().getCanonicalName(), t.getId() });
				return Response.status(BAD_REQUEST).entity(new RestErrorResponse(MSG_INVALID_ID)).build();
			} else if (t instanceof HasAllowedDivisions) {
				BigInteger visibleDivisionsBitmask = Context.getSessionData().getVisibleDivisionsBitmask();
				BigInteger and = ((HasAllowedDivisions) t).getWriteAllowedDivisions().and(visibleDivisionsBitmask);
				if (and.compareTo(BigInteger.ZERO) == 0) {
					logger.info("Not allowed to update. class: {}, id: {}", new Object[] {
							t.getClass().getCanonicalName(), t.getId() });
					return Response.status(METHOD_NOT_ALLOWED).entity(new RestErrorResponse("Update not allowed!"))
							.build();
				}
			}

			populateEntity(t);
			t.updateFromOld(oldT);

			Session session = HibernateUtil.getCurrentSession();
			t = (T) session.merge(t);
			session.flush();

			return Response.status(OK).entity(t).build();
		} catch (ConstraintViolationException e) {
			return handleConstraintViolationExceptionOnPut(e, t);
		} catch (Exception e) {
			logger.error("Error while updating entity", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
					.build();
		}
	}

	protected <T extends Entity> Response handleConstraintViolationExceptionOnPut(ConstraintViolationException e, T t) {
		logger.debug("Constraint violation exception while inserting/updating entity", e);
		HibernateUtil.rollback();
		return Response.status(CONFLICT).entity(new RestErrorResponse(MSG_CONSTRINT_VIOLATED_ON_PUT)).build();
	}

	protected <T extends Entity> Response handleConstraintViolationExceptionOnDelete(ConstraintViolationException e, T t) {
		logger.debug("Constraint violation exception while deleting entity", e);
		HibernateUtil.rollback();
		return Response.status(CONFLICT).entity(new RestErrorResponse(MSG_CONSTRINT_VIOLATED_ON_DELETE)).build();
	}

	/**
	 * This method takes an object, finds all the fields that have inherited
	 * from {@link Entity} and replace them with real objects fetched from the
	 * database. This is to allow the clients to send only skeleton object for
	 * fields to reduce network traffic.
	 * 
	 * @param object
	 *            object to populate
	 */
	@SuppressWarnings("unchecked")
	protected <T extends Entity> void populateEntity(T object) {

		if (object instanceof HasAllowedDivisions) {
			((HasAllowedDivisions) object).populateAllowedDivisions();
		}
		if (object instanceof BelongsToDivision) {
			((BelongsToDivision) object).populateDivision();
		}

		Class<?> clazz = object.getClass();
		for (Field field : getFieldsUp(clazz)) {
			Class<?> typeClass = field.getType();

			if (Entity.class.isAssignableFrom(typeClass)) {
				try {
					field.setAccessible(true);
					// get the entity
					Entity entity = (Entity) field.get(object);
					if (entity != null) {
						Integer id = entity.getId();

						if (id != null && id > 0) {
							GenericDAO<Entity> dao = getDAO((Class<Entity>) typeClass);
							entity = dao.get(id);
							field.set(object, entity);
						} else if (HasNameCode.class.isAssignableFrom(typeClass)) {
							HasNameCode codeEntity = (HasNameCode) entity;
							String code = codeEntity.getCode();
							if (code != null && !code.isEmpty()) {
								Class<HasNameCode> nameCodeClass = (Class<HasNameCode>) typeClass;
								HasCodeDAO<HasNameCode> dao = getHasCodeDAOWorkaround(nameCodeClass);
								codeEntity = dao.get(code);
								field.set(object, codeEntity);
							} else {
								field.set(object, null);
							}
						} else {
							field.set(object, null);
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					logger.error("Error while populating the entity", e);
				}
			} else if (List.class.isAssignableFrom(typeClass) || Set.class.isAssignableFrom(typeClass)) {
				try {
					field.setAccessible(true);
					// get the List or Set
					Collection<?> collection = (Collection<?>) field.get(object);
					if (collection != null) {
						// create a new List or Set
						Collection<Object> newCollection = null;
						if (typeClass.isInterface()) {
							if (List.class.isAssignableFrom(typeClass)) {
								newCollection = new ArrayList<Object>();
							} else if (Set.class.isAssignableFrom(typeClass)) {
								newCollection = new HashSet<Object>();
							}
						} else {
							newCollection = ((Constructor<Collection<Object>>) typeClass.getConstructor())
									.newInstance();
						}

						for (Object item : collection) {
							// populate entities and add to the new List or Set
							if (Entity.class.isAssignableFrom(item.getClass())) {
								Entity entity = (Entity) item;
								Integer id = entity.getId();

								if (id != null && id > 0) {
									GenericDAO<Entity> dao = getDAO((Class<Entity>) item.getClass());

									entity = dao.get(id);
									newCollection.add(entity);
								}
							} else { // add others anyway
								newCollection.add(item);
							}
						}
						// set the new List or Set
						field.set(object, newCollection);
					}
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException
						| InstantiationException | InvocationTargetException e) {
					logger.error("Error while populating an entity in a collection", e);
				}
			}
		}
	}

	protected static Iterable<Field> getFieldsUp(Class<?> startClass) {
		List<Field> currentClassFields = new ArrayList<Field>(Arrays.asList(startClass.getDeclaredFields()));
		Class<?> parentClass = startClass.getSuperclass();

		if (parentClass != null && !(parentClass.equals(Object.class))) {
			List<Field> parentClassFields = (List<Field>) getFieldsUp(parentClass);
			currentClassFields.addAll(parentClassFields);
		}

		return currentClassFields;
	}

	protected <T extends Entity> Response deleteEntity(Class<T> clazz, String idString) {
		Integer id = null;
		try {
			id = Integer.valueOf(idString);
		} catch (NumberFormatException nfe) {
			return Response.status(BAD_REQUEST).entity(new RestErrorResponse(MSG_INVALID_ID)).build();
		}
		logger.info("Deleting the entity. class: {}, id: {}", new Object[] { clazz.getCanonicalName(), idString });
		T t = null;
		try {
			t = load(clazz, id);
			if (t == null) {
				logger.info("Entity not found for deletion. class: {}, id: {}", new Object[] {
						clazz.getCanonicalName(), idString });
				return Response.status(BAD_REQUEST).entity(new RestErrorResponse(MSG_INVALID_ID)).build();
			} else if (t instanceof HasAllowedDivisions) {
				BigInteger visibleDivisionsBitmask = Context.getSessionData().getVisibleDivisionsBitmask();
				BigInteger and = ((HasAllowedDivisions) t).getWriteAllowedDivisions().and(visibleDivisionsBitmask);
				if (and.compareTo(BigInteger.ZERO) == 0) {
					logger.info("Not allowed to delete. class: {}, id: {}", new Object[] {
							t.getClass().getCanonicalName(), t.getId() });
					return Response.status(METHOD_NOT_ALLOWED).entity(new RestErrorResponse("Delete not allowed!"))
							.build();
				}
			}
			delete(t);
			HibernateUtil.commit();
			return Response.status(OK).build();
		} catch (ConstraintViolationException e) {
			return handleConstraintViolationExceptionOnDelete(e, t);
		} catch (Exception e) {
			logger.error("Error while deleting entity", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
					.build();
		}
	}

	protected <T extends Entity & HasNameCode> Response deleteEntityByCode(Class<T> clazz, String code) {
		logger.info("Deleting the entity. class: {}, code: {}", new Object[] { clazz.getCanonicalName(), code });
		T t = null;
		try {
			t = load(clazz, code);
			if (t == null) {
				logger.info("Entity not found for deletion. class: {}, code: {}",
						new Object[] { clazz.getCanonicalName(), code });
				return Response.status(BAD_REQUEST).entity(new RestErrorResponse(MSG_INVALID_CODE)).build();
			} else if (t instanceof HasAllowedDivisions) {
				BigInteger visibleDivisionsBitmask = Context.getSessionData().getVisibleDivisionsBitmask();
				BigInteger and = ((HasAllowedDivisions) t).getWriteAllowedDivisions().and(visibleDivisionsBitmask);
				if (and.compareTo(BigInteger.ZERO) == 0) {
					logger.info("Not allowed to delete. class: {}, id: {}", new Object[] {
							t.getClass().getCanonicalName(), t.getId() });
					return Response.status(METHOD_NOT_ALLOWED).entity(new RestErrorResponse("Delete not allowed!"))
							.build();
				}
			}
			delete(t);
			HibernateUtil.commit();
			return Response.status(OK).build();
		} catch (ConstraintViolationException e) {
			return handleConstraintViolationExceptionOnDelete(e, t);
		} catch (Exception e) {
			logger.error("Error while deleting entity", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
					.build();
		}
	}
}
