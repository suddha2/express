package lk.express.admin;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.Context;

@Entity
@Table(name = "staff")
@XmlType(name = "Staff", namespace = "http://bean.express.lk")
@XmlRootElement
public class Staff extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id")
	private User user;
	@Column(name = "staff_number")
	private String staffNumber;

	public Staff() {
		user = new User();
		User creator = Context.getSessionData().getUser();
		if (creator != null) {
			user.setDivision(creator.getDivision());
		}
	}

	public Staff(User user) {
		this.user = user;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getStaffNumber() {
		return staffNumber;
	}

	public void setStaffNumber(String staffNumber) {
		this.staffNumber = staffNumber;
	}
}
