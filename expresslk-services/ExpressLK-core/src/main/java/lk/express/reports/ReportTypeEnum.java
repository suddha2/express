package lk.express.reports;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ReportTypeEnum", namespace = "http://reports.express.lk")
@XmlRootElement
public enum ReportTypeEnum {
	AgentCollection, SupplierBanking, ScheduleBooking, BusBookingSummary, PartnerBooking, SupplierCollection, AgentDetailed;
}
