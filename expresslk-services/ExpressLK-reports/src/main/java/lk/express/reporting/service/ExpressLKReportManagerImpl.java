package lk.express.reporting.service;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebService;

import lk.express.reporting.GenerateReportResponse;
import lk.express.reporting.ReportCriteria;
import lk.express.reporting.ReportManager;
import lk.express.reporting.ReportTypesResponse;
import lk.express.service.AbstractService;

@HandlerChain(file = "../../../../handler_chains.xml")
@WebService(endpointInterface = "lk.express.reporting.service.ExpressLKReportManager")
public class ExpressLKReportManagerImpl extends AbstractService implements ExpressLKReportManager {

	@Override
	@WebMethod
	public GenerateReportResponse generateReport(ReportCriteria criteria) {
		ReportManager reportManager = new ReportManager();
		return reportManager.generateReport(criteria);
	}

	@Override
	@WebMethod
	public ReportTypesResponse getReportTypes() {
		ReportManager reportManager = new ReportManager();
		return reportManager.getReportTypes();
	}

	@Override
	@WebMethod
	public boolean heartBeat() {
		return super.heartBeat();
	}
}
