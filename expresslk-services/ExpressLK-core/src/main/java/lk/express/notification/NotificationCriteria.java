package lk.express.notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationCriteria {

	public enum NotificationType {
		EMAIL, SMS
	}

	private List<NotificationType> types;
	private NotificationMessage message;
	private String address;

	public NotificationCriteria() {

	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<NotificationType> getTypes() {
		if (types == null) {
			types = new ArrayList<>();
		}
		return types;
	}

	public void setTypes(List<NotificationType> types) {
		this.types = types;
	}

	public NotificationMessage getMessage() {
		return message;
	}

	public void setMessage(NotificationMessage message) {
		this.message = message;
	}
}
