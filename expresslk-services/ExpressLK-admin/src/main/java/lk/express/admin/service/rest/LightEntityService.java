package lk.express.admin.service.rest;

import javax.ws.rs.core.Response;

import lk.express.bean.LightEntity;

public abstract class LightEntityService<T extends LightEntity> extends EntityService<T> {

	public LightEntityService(Class<T> clazz) {
		super(clazz);
	}

	@Override
	public Response deleteEntity(String id) {
		throw new UnsupportedOperationException("DELETE operation is not supported for Light Entities");
	}

	@Override
	public Response putEntity(T t) {
		throw new UnsupportedOperationException("PUT/POST operation is not supported for Light Entities");
	}
}
