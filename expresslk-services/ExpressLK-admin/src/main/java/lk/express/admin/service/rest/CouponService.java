package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.Coupon;

@Path("/admin/coupon")
public class CouponService extends EntityService<Coupon> {

	public CouponService() {
		super(Coupon.class);
	}
}
