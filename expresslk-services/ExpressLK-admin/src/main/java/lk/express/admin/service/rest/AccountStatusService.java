package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.admin.AccountStatus;

@Path("/admin/accountStatus")
public class AccountStatusService extends HasCodeEntityService<AccountStatus> {

	public AccountStatusService() {
		super(AccountStatus.class);
	}
}
