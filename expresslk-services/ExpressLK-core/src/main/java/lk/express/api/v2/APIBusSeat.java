package lk.express.api.v2;

import lk.express.bean.BusSeat;

public class APIBusSeat extends APIEntity {

	public String number;
	public Integer x;
	public Integer y;
	public String seatType;

	/**
	 * @exclude
	 */
	public APIBusSeat() {

	}

	/**
	 * @exclude
	 */
	public APIBusSeat(BusSeat e) {
		super(e);
		number = e.getNumber();
		x = e.getX();
		y = e.getY();
		seatType = e.getSeatType();
	}
}
