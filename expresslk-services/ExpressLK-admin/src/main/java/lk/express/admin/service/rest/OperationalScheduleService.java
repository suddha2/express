package lk.express.admin.service.rest;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import lk.express.bean.Entity;
import lk.express.schedule.OperationalSchedule;

@Path("/admin/operationalSchedule")
public class OperationalScheduleService extends HasDepartureArrivalService<OperationalSchedule> {

	public OperationalScheduleService() {
		super(OperationalSchedule.class);
	}

	@Override
	protected <T extends Entity> Response deleteEntity(Class<T> clazz, String idString) {
		return Response.status(Status.FORBIDDEN).build();
	}

	@Override
	protected <T extends Entity> Response insertEntity(T t) {
		return Response.status(Status.FORBIDDEN).build();
	}

	@Override
	protected <T extends Entity> Response updateEntity(T t) {
		return Response.status(Status.FORBIDDEN).build();
	}
}
