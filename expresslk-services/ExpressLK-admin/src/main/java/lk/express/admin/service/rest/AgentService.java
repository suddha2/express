package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.admin.Agent;

@Path("/admin/agent")
public class AgentService extends EntityService<Agent> {

	public AgentService() {
		super(Agent.class);
	}
}
