package lk.express.bean;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Gender", namespace = "http://bean.express.lk")
@XmlRootElement
public enum Gender {
	Male, Female, Other
}
