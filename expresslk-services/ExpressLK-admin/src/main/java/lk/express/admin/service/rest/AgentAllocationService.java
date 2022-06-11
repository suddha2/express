package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.admin.AgentAllocation;

@Path("/admin/agentAllocation")
public class AgentAllocationService extends EntityService<AgentAllocation> {

	public AgentAllocationService() {
		super(AgentAllocation.class);
	}
}
