package lk.express;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.City;

/**
 * A <code>ResultLeg</code> represents a single result for a particular leg.
 */
@XmlRootElement
@XmlType(name = "ResultLeg", namespace = "http://express.lk")
@XmlSeeAlso({ ResultSector.class, City.class })
public class ResultLeg extends Result implements HasDepartureArrival {

	private static final long serialVersionUID = 1L;

	private List<ResultSector> sectors = new ArrayList<ResultSector>();

	public City getFromCity() {
		if (!sectors.isEmpty()) {
			return sectors.get(0).getFromCity();
		}
		return null;
	}

	public City getToCity() {
		if (!sectors.isEmpty()) {
			return sectors.get(sectors.size() - 1).getToCity();
		}
		return null;
	}

	@Override
	public Date getDepartureTime() {
		if (!sectors.isEmpty()) {
			return sectors.get(0).getDepartureTime();
		}
		return null;
	}

	@Override
	public Date getArrivalTime() {
		if (!sectors.isEmpty()) {
			return sectors.get(sectors.size() - 1).getArrivalTime();
		}
		return null;
	}

	public int getNoOfTransits() {
		return sectors.size() - 1;
	}

	public List<ResultSector> getSectors() {
		return sectors;
	}

	public void setSectors(List<ResultSector> sectors) {
		this.sectors = sectors;
	}

	public void arrangeCostPrices() {
		double totalFare = 0d;
		double totalCost = 0d;
		double totalPrice = 0d;

		double totalChildFare = 0d;
		double totalChildCost = 0d;
		double totalChildPrice = 0d;

		for (ResultSector sector : sectors) {
			totalFare += sector.getFare();
			totalCost += sector.getCost();
			totalPrice += sector.getPrice();

			totalChildFare += sector.getChildFare();
			totalChildCost += sector.getChildCost();
			totalChildPrice += sector.getChildPrice();

		}
		setFare(totalFare);
		setCost(totalCost);
		setPrice(totalPrice);
		setChildFare(totalChildFare);
		setChildCost(totalChildCost);
		setChildPrice(totalChildPrice);

	}
}
