package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.rule.bean.Rule;

@Path("/admin/rule")
public class RuleService extends EntityService<Rule> {

	public RuleService() {
		super(Rule.class);
	}
}
