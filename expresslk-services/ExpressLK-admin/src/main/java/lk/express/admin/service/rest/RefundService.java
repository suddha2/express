package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.Context;
import lk.express.bean.Entity;
import lk.express.bean.Refund;

@Path("/admin/refund")
public class RefundService extends EntityService<Refund> {

	public RefundService() {
		super(Refund.class);
	}

	@Override
	protected <T extends Entity> void populateEntity(T object) {
		Refund refund = (Refund) object;
		if (refund.getUserId() == null || refund.getUserId() < 0) {
			refund.setUserId(Context.getSessionData().getUser().getId());
		}

		super.populateEntity(object);
	}
}
