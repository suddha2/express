package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "ClientDetail", namespace = "http://express.lk")
public class ClientDetail extends PersonDetail {

}
