package lk.express;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * A <code>LegResult</code> contains all the results for a particular leg that
 * was searched.
 */
@XmlRootElement
@XmlType(name = "LegResult", namespace = "http://express.lk")
@XmlSeeAlso({ ResultLeg.class })
public class LegResult {

	private List<ResultLeg> legs = new ArrayList<ResultLeg>();

	public LegResult() {
	}

	public LegResult(List<ResultLeg> legs) {
		this.legs = legs;
	}

	public List<ResultLeg> getLegs() {
		return legs;
	}

	public void setLegs(List<ResultLeg> legs) {
		this.legs = legs;
	}
}
