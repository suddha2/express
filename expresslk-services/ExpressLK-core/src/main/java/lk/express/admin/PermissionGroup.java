package lk.express.admin;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DiscriminatorValue("PermissionGroup")
@XmlType(name = "PermissionGroup", namespace = "http://admin.express.lk")
@XmlRootElement
public class PermissionGroup extends Permission {

	private static final long serialVersionUID = 1L;

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = { CascadeType.REFRESH })
	@JoinTable(name = "permission_group_permission", joinColumns = { @JoinColumn(name = "permission_group_id") }, inverseJoinColumns = { @JoinColumn(name = "permission_id") })
	private List<PermissionSingle> permissions;

	public List<PermissionSingle> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<PermissionSingle> permissions) {
		this.permissions = permissions;
	}

	@Override
	public boolean match(String code) {
		if (getCode().equals(code)) {
			return true;
		} else {
			for (PermissionSingle permission : permissions) {
				if (permission.getCode().equals(code)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	@JsonIgnore
	public List<String> getCodes() {
		return permissions.stream().map(p -> p.getCode()).collect(Collectors.toList());
	}
}
