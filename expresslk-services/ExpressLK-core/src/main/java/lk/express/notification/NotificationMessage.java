package lk.express.notification;

import java.util.List;

/**
 * @author dilantha
 *
 */
public class NotificationMessage {

	private String body;
	private String title;
	private List<String> attachments;

	public NotificationMessage() {

	}

	public String getBody() {
		return body;
	}

	public void setBody(String message) {
		this.body = message;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}

}
