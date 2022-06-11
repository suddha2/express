package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import lk.express.CRUD;
import lk.express.bean.BusImage;
import lk.express.db.HibernateUtil;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/admin/busImage")
public class BusImageService extends EntityService<BusImage> {

	private static final Logger logger = LoggerFactory.getLogger(BusImageService.class);

	public BusImageService() {
		super(BusImage.class);
	}

	@GET
	@Path("/getImage/{id}")
	@Produces("image/png")
	public Response getImage(@PathParam("id") String id) {
		try {
			Response response = getEntity(BusImage.class, id);
			if (response.getStatus() == OK.getStatusCode()) {
				byte[] image = ((BusImage) response.getEntity()).getImage();
				return Response.status(OK).entity(new StreamingOutput() {
					@Override
					public void write(OutputStream output) throws IOException, WebApplicationException {
						output.write(image);
						output.flush();
						output.close();
					}
				}).build();
			}
			return response;
		} catch (Exception e) {
			logger.error("Error while retrieving the image", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(MSG_INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/uploadImage")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormParam("busId") String busIdString,
			@FormDataParam("image") InputStream uploadedStream) {
		try {
			if (authHandler.isAllowed(httpHeaders, CRUD.Create)) {
				Integer busId = null;
				try {
					busId = Integer.valueOf(busIdString);
				} catch (NumberFormatException nfe) {
					return Response.status(BAD_REQUEST).entity(new RestErrorResponse(MSG_INVALID_ID)).build();
				}

				BusImage busImage = new BusImage();
				busImage.setBusId(busId);
				busImage.setImage(IOUtils.toByteArray(uploadedStream));
				return insertEntity(busImage);
			} else {
				return authHandler.buildErrorResponse(httpHeaders, CRUD.Create);
			}
		} catch (Exception e) {
			logger.error("Error while uploading the image", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(MSG_INTERNAL_SERVER_ERROR).build();
		}
	}
}
