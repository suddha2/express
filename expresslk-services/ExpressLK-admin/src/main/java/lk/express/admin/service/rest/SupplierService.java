package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.OK;

import java.math.BigInteger;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import lk.express.db.HibernateUtil;
import lk.express.supplier.Supplier;

@Path("/admin/supplier")
public class SupplierService extends EntityService<Supplier> {

	public SupplierService() {
		super(Supplier.class);
	}

	@GET
	@Path("/list/{bitmask}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDepotFareReport(@PathParam("bitmask") String bitmaskSum) {

		Session session = HibernateUtil.getCurrentSession();
		 String sql = " select id, name from supplier where allowed_divisions & "+
		  " :bitmaskSum >0 order by name ";
//		String sql = "select supplier.id,supplier.name  from user_group_division "
//				+ " join division on user_group_division.division_id = division.id  "
//				+ " join supplier on division.bitmask=supplier.allowed_divisions where user_group_id=:groupId";
		List<?> collection = session.createSQLQuery(sql).setBigInteger("bitmaskSum", new BigInteger(bitmaskSum))
				.setResultTransformer(Transformers.aliasToBean(SupplierList.class)).list();
		return Response.status(OK).entity(collection).build();
	}

	public static final class SupplierList {
		private int id;
		private String name;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
}
