package lk.express.admin;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Entity
@DiscriminatorValue("PermissionSingle")
@XmlType(name = "PermissionSingle", namespace = "http://admin.express.lk")
@XmlRootElement
public class PermissionSingle extends Permission {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean match(String code) {
		return getCode().equals(code);
	}
}
