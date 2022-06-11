package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "ExpResponse", namespace = "http://express.lk")
public class ExpResponse<T> {

	public static int SUCCESS = 1;
	public static int FAIL = -1;
	public static int SESSION_EXPIRED = -2;
	public static int ACCESS_DENIED = -3;

	private int status;
	private String message;
	private T data;

	public ExpResponse() {
		this.status = SUCCESS;
	}

	public ExpResponse(int status) {
		this.status = status;
	}

	public ExpResponse(String errorMessage) {
		this.status = FAIL;
		this.message = errorMessage;
	}

	public ExpResponse(T data) {
		this.status = SUCCESS;
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
