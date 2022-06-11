package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.RefundRequest;

@Path("/admin/refundRequest")
public class RefundRequestService extends EntityService<RefundRequest> {

	public RefundRequestService() {
		super(RefundRequest.class);
	}
}
