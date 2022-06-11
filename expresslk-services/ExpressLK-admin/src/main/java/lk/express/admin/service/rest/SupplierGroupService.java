package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.supplier.SupplierGroup;

@Path("/admin/supplierGroup")
public class SupplierGroupService extends EntityService<SupplierGroup> {

	public SupplierGroupService() {
		super(SupplierGroup.class);
	}
}
