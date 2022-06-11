package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.supplier.SupplierContactPerson;

@Path("/admin/supplierContactPerson")
public class SupplierContactPersonService extends HasPersonService<SupplierContactPerson> {

	public SupplierContactPersonService() {
		super(SupplierContactPerson.class);
	}
}
