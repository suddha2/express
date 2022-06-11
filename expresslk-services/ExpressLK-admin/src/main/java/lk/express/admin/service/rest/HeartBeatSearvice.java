package lk.express.admin.service.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import lk.express.service.RestService;

@Path("/admin/heartBeat")
public class HeartBeatSearvice extends RestService {

	@GET
	public Response checkHeartBeat() {
		boolean up = super.heartBeat();
		if (up) {
			return Response.ok(new RestResponse("live")).build();
		} else {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE)
					.entity(new RestErrorResponse(Response.Status.SERVICE_UNAVAILABLE.getReasonPhrase())).build();
		}
	}
}
