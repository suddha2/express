package lk.express;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "ConfirmationCriteria", namespace = "http://express.lk")
public class ConfirmationCriteria {

	private ClientDetail client;
	/**
	 * @deprecated
	 */
	@Deprecated
	private int agentId;
	private List<PassengerDetail> passengers;
	private List<SeatAllocations> itemSeatAllocations;
	private PaymentMethodCriteria paymentMethodCriteria;
	private List<PaymentCriteria> paymentCriteria;
	private String remarks;

	public ClientDetail getClient() {
		return client;
	}

	public void setClient(ClientDetail client) {
		this.client = client;
	}

	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	public List<PassengerDetail> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<PassengerDetail> passengers) {
		this.passengers = passengers;
	}

	public List<SeatAllocations> getItemSeatAllocations() {
		return itemSeatAllocations;
	}

	public SeatAllocations getItemSeatAllocations(int heldItemId) {
		for (SeatAllocations a : getItemSeatAllocations()) {
			if (a.getHeldItemId() == heldItemId) {
				return a;
			}
		}
		return null;
	}

	public void setItemSeatAllocations(List<SeatAllocations> itemSeatAllocations) {
		this.itemSeatAllocations = itemSeatAllocations;
	}

	@XmlElement(nillable = true)
	public PaymentMethodCriteria getPaymentMethodCriteria() {
		return paymentMethodCriteria;
	}

	public void setPaymentMethodCriteria(PaymentMethodCriteria paymentMethodCriteria) {
		this.paymentMethodCriteria = paymentMethodCriteria;
	}

	@XmlElement(nillable = true)
	public List<PaymentCriteria> getPaymentCriteria() {
		return paymentCriteria;
	}

	public void setPaymentCriteria(List<PaymentCriteria> paymentCriteria) {
		this.paymentCriteria = paymentCriteria;
	}

	@XmlElement(nillable = true)
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
