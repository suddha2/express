package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.admin.AgentRestriction;

@Path("/admin/agentRestriction")
public class AgentRestrictionService extends EntityService<AgentRestriction> {

	public AgentRestrictionService() {
		super(AgentRestriction.class);
	}

}
