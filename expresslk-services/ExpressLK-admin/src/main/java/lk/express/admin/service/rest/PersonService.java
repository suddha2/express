package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.CONFLICT;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import lk.express.admin.Person;
import lk.express.bean.Entity;
import lk.express.db.HibernateUtil;

import org.hibernate.Criteria;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/admin/person")
public class PersonService extends EntityService<Person> {

	private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

	public PersonService() {
		super(Person.class);
	}

	@Override
	protected <T extends Entity> Response handleConstraintViolationExceptionOnPut(ConstraintViolationException e, T t) {
		logger.error(
				"Constraint violation exception while inserting/updating entity: A person with the same NIC already exists!",
				e);
		HibernateUtil.rollback();
		return Response.status(CONFLICT).entity(new RestErrorResponse("A person with the same NIC already exists!"))
				.build();
	}

	@Override
	protected <T extends Entity> void populateCriteria(Class<T> clazz, Criteria criteria,
			Map<String, String> pathToAlias, Map<String, List<String>> queryParams) {

		(new SupplierContactPersonService()).populateCriteria(clazz, criteria, pathToAlias, queryParams, null);
		super.populateCriteria(clazz, criteria, pathToAlias, queryParams);
	}

	@Override
	protected <T extends Entity> void addSortAndLimitToCriteria(Class<T> clazz, Criteria criteria,
			Map<String, String> pathToAlias, Map<String, List<String>> queryParams) {

		(new SupplierContactPersonService()).addSortAndLimitToCriteria(clazz, criteria, pathToAlias, queryParams, null);
		super.addSortAndLimitToCriteria(clazz, criteria, pathToAlias, queryParams);
	}
}
