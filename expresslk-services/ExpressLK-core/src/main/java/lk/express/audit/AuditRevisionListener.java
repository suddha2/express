package lk.express.audit;

import lk.express.Context;

import org.hibernate.envers.RevisionListener;

public class AuditRevisionListener implements RevisionListener {

	@Override
	public void newRevision(Object revision) {
		AuditRevision auditRevision = (AuditRevision) revision;
		auditRevision.setUsername(Context.getSessionData().getUsername());
	}
}
