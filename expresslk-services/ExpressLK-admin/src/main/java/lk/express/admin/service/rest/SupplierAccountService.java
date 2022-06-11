package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.supplier.SupplierAccount;

@Path("/admin/supplierAccount")
public class SupplierAccountService extends EntityService<SupplierAccount> {

	public SupplierAccountService() {
		super(SupplierAccount.class);
	}
}
