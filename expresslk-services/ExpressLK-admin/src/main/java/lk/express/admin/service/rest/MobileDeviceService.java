package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.supplier.MobileDevice;

@Path("/admin/mobileDevice")
public class MobileDeviceService extends EntityService<MobileDevice> {

	public MobileDeviceService() {
		super(MobileDevice.class);
	}
}
