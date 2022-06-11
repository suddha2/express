package lk.express.reporting.service;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import lk.express.reporting.GenerateReportResponse;
import lk.express.reporting.ReportCriteria;
import lk.express.reporting.ReportTypesResponse;

@WebService
@SOAPBinding(style = Style.RPC)
public interface ExpressLKReportManager {

	@WebMethod
	public GenerateReportResponse generateReport(ReportCriteria criteria);

	@WebMethod
	public ReportTypesResponse getReportTypes();

	@WebMethod
	public boolean heartBeat();
}
