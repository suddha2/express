package lk.express.audit;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@Table(name = "REVINFO")
@RevisionEntity(AuditRevisionListener.class)
public class AuditRevision implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@RevisionNumber
	@Column(name = "REV")
	private int id;

	@RevisionTimestamp
	@Column(name = "REVTSTMP")
	private long timestamp;

	@Column(name = "USERNAME")
	private String username;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Transient
	public Date getRevisionDate() {
		return new Date(timestamp);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AuditRevision)) {
			return false;
		}

		final AuditRevision that = (AuditRevision) o;
		return id == that.id && timestamp == that.timestamp;
	}

	@Override
	public int hashCode() {
		int result;
		result = id;
		result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "RevisionEntity(id = " + id + ", revisionDate = "
				+ DateFormat.getDateTimeInstance().format(getRevisionDate()) + ")";
	}
}
