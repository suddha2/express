package lk.express.rule;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ApplicationType", namespace = "http://rule.express.lk")
@XmlRootElement
public enum ApplicationType {
	Percentage, Absolute;

	public static ApplicationType getByString(String string) {
		for (ApplicationType type : values()) {
			if (type.name().equalsIgnoreCase(string)) {
				return type;
			}
		}
		return Percentage;
	}
}