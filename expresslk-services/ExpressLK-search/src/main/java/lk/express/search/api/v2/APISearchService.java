package lk.express.search.api.v2;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.io.IOException;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import lk.express.AvailabilityCriteria;
import lk.express.AvailabilityResponse;
import lk.express.ConfirmResponse;
import lk.express.ExpResponse;
import lk.express.HoldResponse;
import lk.express.PaymentRefundsResponse;
import lk.express.SearchResponse;
import lk.express.SessionCriteria;
import lk.express.admin.AgentAllocation;
import lk.express.api.v2.APIPayment;
import lk.express.auth.rest.RESTAuthenticationHandler;
import lk.express.bean.Payment;
import lk.express.search.api.v2.APIHoldCriteria.APISeatCriteria;
import lk.express.search.service.ExpressLKSearch;
import lk.express.search.service.ExpressLKSearchImpl;
import lk.express.service.RestService;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/search")
public class APISearchService extends RestService {

	private static final String SEARCH_SESSION = "SearchSession";
	private static ExpressLKSearch search = new ExpressLKSearchImpl();

	protected RESTAuthenticationHandler authHandler;
	protected @Context HttpHeaders httpHeaders;

	public APISearchService() {
		this.authHandler = new RESTAuthenticationHandler(APISearchService.class);
	}

	private Response getResponse(ExpResponse<?> response) {
		if (response.getStatus() == ExpResponse.SUCCESS) {
			return Response.ok().entity(response.getData()).build();
		} else if (response.getStatus() == ExpResponse.SESSION_EXPIRED) {
			return Response.status(UNAUTHORIZED).entity(new RestErrorResponse(response.getMessage())).build();
		} else if (response.getStatus() == ExpResponse.ACCESS_DENIED) {
			return Response.status(FORBIDDEN).entity(new RestErrorResponse(response.getMessage())).build();
		}
		return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(response.getMessage())).build();
	}

	@GET
	@Path("/coffee")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response coffee() {
		return Response.status(418).entity(new RestErrorResponse("I am a teapot and cannot brew coffee at this time."))
				.build();
	}

	@GET
	@Path("/teapot")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response teapot() {
		return Response
				.status(418)
				.entity(new RestErrorResponse(
						"The requested entity body is short and stout. Tip me over and pour me out.")).build();
	}

	@POST
	@Path("/createSession")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createSession() {
		String method = "createSession";
		if (authHandler.isAllowed(httpHeaders, method)) {
			SessionCriteria criteria = new SessionCriteria();
			criteria.setLocale(SessionCriteria.LOCALE_EN);
			String sessionId = search.createSession(criteria);
			if (sessionId != null) {
				NewCookie cookie = new NewCookie(SEARCH_SESSION, sessionId);
				return Response.ok().cookie(cookie).build();
			} else {
				return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
						.build();
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, method);
		}
	}

	@GET
	@Path("/search/{criteriaJson}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response search(@CookieParam(SEARCH_SESSION) String sessionId, @PathParam("criteriaJson") String criteriaJson) {
		String method = "search";
		if (authHandler.isAllowed(httpHeaders, method)) {
			APISearchCriteria criteria = new APISearchCriteria();
			SearchResponse response;

			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.readerForUpdating(criteria).readValue(criteriaJson);
				String error = criteria.validate();
				if (error != null) {
					return Response.status(BAD_REQUEST).entity(new RestErrorResponse(error)).build();
				}
				response = search.search(sessionId, criteria.getCriteria());
			} catch (Exception e) {
				return Response.status(BAD_REQUEST).build();
			}

			if (response.getStatus() > 0) {
				APISearchResult result = new APISearchResult(response.getData());
				return Response.ok().entity(result).build();
			} else {
				return getResponse(response);
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, method);
		}
	}

	@GET
	@Path("/checkAvailability/{criteriaJson}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response checkAvailability(@CookieParam(SEARCH_SESSION) String sessionId,
			@PathParam("criteriaJson") String criteriaJson) {
		String method = "checkAvailability";
		if (authHandler.isAllowed(httpHeaders, method)) {
			APIAvailabilityCriteria criteria = new APIAvailabilityCriteria();
			AvailabilityResponse response;

			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.readerForUpdating(criteria).readValue(criteriaJson);
				String error = criteria.validate();
				if (error != null) {
					return Response.status(BAD_REQUEST).entity(new RestErrorResponse(error)).build();
				}
				response = search.checkAvailability(sessionId, criteria.getCriteria());
			} catch (IOException e) {
				return Response.status(BAD_REQUEST).build();
			}

			if (response.getStatus() > 0) {
				APIAvailabilityResult result = new APIAvailabilityResult(response.getData());
				return Response.ok().entity(result).build();
			} else {
				return getResponse(response);
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, method);
		}
	}

	@POST
	@Path("/hold")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response hold(@CookieParam(SEARCH_SESSION) String sessionId, @FormParam("criteria") String criteriaJson) {
		String method = "hold";
		if (authHandler.isAllowed(httpHeaders, method)) {
			APIHoldCriteria criteria = new APIHoldCriteria();
			HoldResponse response;

			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.readerForUpdating(criteria).readValue(criteriaJson);
				String error = criteria.validate();
				if (error != null) {
					return Response.status(BAD_REQUEST).entity(new RestErrorResponse(error)).build();
				}

				// Disallow booking agent seats
				for (APISeatCriteria seatCriteria : criteria.seatCriterias) {
					AvailabilityCriteria avCriteria = new AvailabilityCriteria();
					avCriteria.setResultIndex(criteria.resultIndex);
					avCriteria.setSectorIndex(seatCriteria.sectorIndex);
					avCriteria.setBoardingLocationId(seatCriteria.boardingLocationId);
					avCriteria.setDroppingLocationId(seatCriteria.dropOffLocationId);
					AvailabilityResponse avResponse = search.checkAvailability(sessionId, avCriteria);
					if (avResponse.getStatus() > 0) {
						for (String seat : seatCriteria.seats) {
							for (AgentAllocation allocation : avResponse.getData().getAgentAllocations()) {
								if (allocation.getSeatNumbers().contains(seat)) {
									return Response.status(BAD_REQUEST)
											.entity(new RestErrorResponse("Some seats are unavailable")).build();
								}
							}
						}
					} else {
						return Response.status(BAD_REQUEST).build();
					}
				}

				response = search.hold(sessionId, criteria.getCriteria());
			} catch (IOException e) {
				return Response.status(BAD_REQUEST).build();
			}

			if (response.getStatus() > 0) {
				APIHoldResult result = new APIHoldResult(response.getData());
				return Response.ok().entity(result).build();
			} else {
				return getResponse(response);
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, method);
		}
	}

	@POST
	@Path("/confirm")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response confirm(@CookieParam(SEARCH_SESSION) String sessionId, @FormParam("criteria") String criteriaJson) {
		String method = "confirm";
		if (authHandler.isAllowed(httpHeaders, method)) {
			APIConfirmCriteria criteria = new APIConfirmCriteria();
			ConfirmResponse response;

			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.readerForUpdating(criteria).readValue(criteriaJson);
				String error = criteria.validate();
				if (error != null) {
					return Response.status(BAD_REQUEST).entity(new RestErrorResponse(error)).build();
				}
				response = search.confirm(sessionId, criteria.getCriteria());
			} catch (IOException e) {
				return Response.status(BAD_REQUEST).build();
			}

			if (response.getStatus() > 0) {
				APIConfirmResult result = new APIConfirmResult(response.getData());
				return Response.ok().entity(result).build();
			} else {
				return getResponse(response);
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, method);
		}
	}

	@POST
	@Path("/addPayment")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response addPayment(@CookieParam(SEARCH_SESSION) String sessionId, @FormParam("criteria") String criteriaJson) {
		String method = "addPayment";
		if (authHandler.isAllowed(httpHeaders, method)) {
			APIPaymentCriteria criteria = new APIPaymentCriteria();
			PaymentRefundsResponse response;

			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.readerForUpdating(criteria).readValue(criteriaJson);
				String error = criteria.validate();
				if (error != null) {
					return Response.status(BAD_REQUEST).entity(new RestErrorResponse(error)).build();
				}
				response = search.addPaymentRefund(criteria.getCriteria());
			} catch (IOException e) {
				return Response.status(BAD_REQUEST).build();
			}

			if (response.getStatus() > 0) {
				APIPayment result = new APIPayment((Payment) response.getData());
				return Response.ok().entity(result).build();
			} else {
				return getResponse(response);
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, method);
		}
	}
}
