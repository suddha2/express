package lk.express.reporting;

import java.util.List;

import lk.express.ExpResponse;
import lk.express.db.HibernateUtil;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;
import lk.express.reporting.ReportTypesResponse.ReportTypesData;
import lk.express.reports.ReportType;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportManager {

	protected static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);
	private static final Logger logger = LoggerFactory.getLogger(ReportManager.class);

	public GenerateReportResponse generateReport(ReportCriteria criteria) {
		try {
			Session dbSession = HibernateUtil.getCurrentSessionWithTransaction();
			IReportGenerator generator = ReportGeneratorFactory
					.getReportGenerator(ReportGeneratorFactory.JASPER_ENGINE);
			return generator.generateReport(criteria, dbSession);
		} catch (Exception e) {
			logger.error("Exception while generating report", e);
			e.printStackTrace();
			GenerateReportResponse response = new GenerateReportResponse();
			response.setStatus(ExpResponse.FAIL);
			response.setMessage(e.getMessage());
			return response;
		}
	}

	public ReportTypesResponse getReportTypes() {
		ReportTypesResponse response = new ReportTypesResponse();
		try {
			GenericDAO<ReportType> reportDao = daoFac.getReportTypeDAO();
			List<ReportType> reportList = reportDao.list();

			response.setStatus(ExpResponse.SUCCESS);
			response.setData(new ReportTypesData(reportList));
		} catch (Exception e) {
			logger.error("Exception while retriving report types", e);
			response.setStatus(ExpResponse.FAIL);
			response.setMessage(e.getMessage());
		}

		return response;
	}
}