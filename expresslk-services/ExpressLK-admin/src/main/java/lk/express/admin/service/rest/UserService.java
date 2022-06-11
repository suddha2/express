package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import lk.express.CRUD;
import lk.express.Context;
import lk.express.admin.AccountStatus;
import lk.express.admin.PasswordResetToken;
import lk.express.admin.Permission;
import lk.express.admin.User;
import lk.express.admin.UserGroup;
import lk.express.api.APIToken;
import lk.express.bean.Entity;
import lk.express.db.HibernateUtil;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;
import lk.express.db.dao.HasUniqueDAO;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/admin/user")
public class UserService extends EntityService<User> {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	private static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

	private static final String MSG_AUTHENTICATION_FAILED = "Authentication failed!";
	private static final String MSG_BAD_PASSWORD = "Supplied password does not meet password criteria";
	private static final String MSG_PERMISSION_ASSIGN = "You can not grant a permission you do not have";

	public UserService() {
		super(User.class);
	}

	@Override
	public Map<String, List<String>> getQueryParameters(UriInfo uriInfo) {
		Map<String, List<String>> queryParams = super.getQueryParameters(uriInfo);
		addAgentRestriction(queryParams);
		return queryParams;
	}

	@Override
	public <T extends Entity> Response getEntity(Class<T> clazz, String idString) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put("id", Arrays.asList(idString));
		addAgentRestriction(queryParams);

		Response response = getEntities(clazz, queryParams);
		Object entities = response.getEntity();
		if (entities != null && entities instanceof List<?>) {
			Object entity = null;
			List<?> list = (List<?>) entities;
			if (!list.isEmpty()) {
				entity = list.get(0);
			}
			return Response.fromResponse(response).entity(entity).build();
		} else {
			return response;
		}
	}

	private void addAgentRestriction(Map<String, List<String>> queryParams) {
		User user = Context.getSessionData().getUser();
		if (user.getAgent() != null) {
			queryParams.put("agent", Arrays.asList(String.valueOf(user.getAgent().getId())));
		}
	}

	/**
	 * Adds or updates a user
	 * 
	 * @param user
	 *            JAXBElement for the user
	 * @return Response object containing the new/updated user
	 */
	@Override
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response putEntity(User user) {

		CRUD operation = user.getId() == null ? CRUD.Create : CRUD.Update;

		if (!authHandler.isAllowed(httpHeaders, operation)) {
			return authHandler.buildErrorResponse(httpHeaders, operation);
		} else {
			Set<String> permissions = new HashSet<>();
			for (UserGroup ug : user.getUserGroups()) {
				for (Permission p : ug.getPermission()) {
					permissions.addAll(p.getCodes());
				}
			}

			if (operation == CRUD.Update) {
				Response response = getEntity(clazz, user.getId().toString());
				if (response.getStatusInfo() == OK) {
					User oldUser = (User) response.getEntity();

					Set<String> oldPermissions = new HashSet<>();
					for (UserGroup ug : oldUser.getUserGroups()) {
						for (Permission p : ug.getPermission()) {
							oldPermissions.addAll(p.getCodes());
						}
					}
					permissions.removeAll(oldPermissions);
					if (!validatePermissions(permissions)) {
						return Response.status(FORBIDDEN).entity(new RestErrorResponse(MSG_PERMISSION_ASSIGN)).build();
					}

					// Use the old password. Do not allow changing it.
					user.setPassword(oldUser.getPassword());
					return updateEntity(user);
				} else {
					return response;
				}
			} else {
				if (!validatePermissions(permissions)) {
					return Response.status(FORBIDDEN).entity(new RestErrorResponse(MSG_PERMISSION_ASSIGN)).build();
				}

				User creator = Context.getSessionData().getUser();
				user.setAccountable(creator.getAccountable());
				user.setAgent(creator.getAgent());
				user.setDivision(creator.getDivision());

				List<String> pw = httpHeaders.getRequestHeader("password");
				String password = null;
				if (pw != null && pw.size() > 0) {
					password = pw.get(0);
				}
				if (!isValidPassword(password)) {
					return Response.status(BAD_REQUEST).entity(new RestErrorResponse(MSG_BAD_PASSWORD)).build();
				} else {
					// Hash a password for the first time
					String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
					user.setPassword(hashed);
					return super.putEntity(user);
				}
			}
		}
	}

	private boolean validatePermissions(Set<String> permissions) {
		User granter = Context.getSessionData().getUser();
		for (String p : permissions) {
			if (!granter.hasPermission(p)) {
				return false;
			}
		}
		return true;
	}

	private boolean isValidPassword(String password) {
		// TODO introduce strict criteria here.
		return password != null && !password.isEmpty();
	}

	/**
	 * Authenticate a user
	 * 
	 * @param username
	 *            username
	 * @param password
	 *            password
	 * @return Authenticated user in the case of successful authentication
	 */
	@POST
	@Path("/authenticate")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response authenticate(@FormParam("username") String username, @FormParam("password") String password) {

		if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
			return Response.status(FORBIDDEN).entity(new RestErrorResponse(MSG_AUTHENTICATION_FAILED)).build();
		}

		try {
			HasUniqueDAO<User> userDAO = daoFac.getUserDAO();
			User user = userDAO.findUnique(new User(username));

			if (user != null && AccountStatus.ACTIVE.equals(user.getStatus().getCode())) {
				if (BCrypt.checkpw(password, user.getPassword())) {
					logger.debug("User authenticated successfully: " + user.getUsername());
					return Response.status(OK).entity(user).build();
				}
			}
			logger.debug("Authentication failed for: " + user.getUsername());
			return Response.status(FORBIDDEN).entity(new RestErrorResponse(MSG_AUTHENTICATION_FAILED)).build();
		} catch (Exception e) {
			logger.error("Error while retrieving entity", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
					.build();
		}
	}

	/**
	 * Returns a short-lived token used for authentication
	 * 
	 * @param username
	 *            username
	 * @param password
	 *            password
	 * @return authentication token
	 */
	@POST
	@Path("/getToken")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getToken(@FormParam("username") String username, @FormParam("password") String password) {

		if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
			return Response.status(FORBIDDEN).entity(new RestErrorResponse(MSG_AUTHENTICATION_FAILED)).build();
		}

		try {
			User ex = new User();
			ex.setUsername(username);
			HasUniqueDAO<User> userDAO = daoFac.getUserDAO();
			User user = userDAO.findUnique(ex);

			if (user != null && AccountStatus.ACTIVE.equals(user.getStatus().getCode())) {
				if (BCrypt.checkpw(password, user.getPassword())) {

					APIToken token = new APIToken();
					token.setUsername(username);
					String t = UUID.randomUUID().toString();
					token.setToken(t);
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.DATE, 1);
					token.setExpiry(calendar.getTime());

					GenericDAO<APIToken> tokenDAO = daoFac.getGenericDAO(APIToken.class);
					tokenDAO.persist(token);

					return Response.status(OK).entity(new TokenResponse(token)).build();
				}
			}
			return Response.status(FORBIDDEN).entity(new RestErrorResponse(MSG_AUTHENTICATION_FAILED)).build();
		} catch (Exception e) {
			logger.error("Error while generating token", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
					.build();
		}
	}

	/**
	 * Verify a token
	 * 
	 * @param username
	 *            username
	 * @param token
	 *            token
	 * @return true if verified
	 */
	@POST
	@Path("/verifyToken")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response verifyToken(@FormParam("username") String username, @FormParam("token") String token) {
		try {
			String user = authHandler.authenticate(username, token);
			if (user != null) {
				return Response.status(OK).entity(new RestResponse(true)).build();
			}
			return Response.status(OK).entity(new RestResponse(false)).build();
		} catch (Exception e) {
			logger.error("Error while verifying token", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
					.build();
		}
	}

	/**
	 * Temporary wrapper class used since the font end does not recognize string
	 * as valid JSON
	 */
	public static final class TokenResponse {

		private String token;
		private Date expiry;

		public TokenResponse() {
		}

		public TokenResponse(APIToken token) {
			this.token = token.getToken();
			this.expiry = token.getExpiry();
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public Date getExpiry() {
			return expiry;
		}

		public void setExpiry(Date expiry) {
			this.expiry = expiry;
		}
	}

	/**
	 * Allow changing password for a user
	 * 
	 * @param username
	 *            username
	 * @param password
	 *            current password
	 * @param newPassword
	 *            new password
	 * @return Response object containing the updated user
	 */
	@POST
	@Path("/changePassword")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response changePassword(@FormParam("username") String username, @FormParam("password") String password,
			@FormParam("newPassword") String newPassword) {
		if (!isValidPassword(newPassword)) {
			return Response.status(BAD_REQUEST).entity(new RestErrorResponse(MSG_BAD_PASSWORD)).build();
		} else {
			Response authResponse = authenticate(username, password);
			if (authResponse.getStatusInfo() == OK) {
				User user = (User) authResponse.getEntity();
				// Hash a password for the first time
				String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
				user.setPassword(hashed);
				return updateEntity(user);
			} else {
				return Response.status(UNAUTHORIZED).entity(new RestErrorResponse(MSG_AUTHENTICATION_FAILED)).build();
			}
		}
	}

	/**
	 * Token a token for resetting the password
	 * 
	 * @param username
	 *            username
	 * @return token
	 */
	@POST
	@Path("/forgotPassword")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response forgotPassword(@FormParam("username") String username) {
		try {
			PasswordResetToken token = new PasswordResetToken();
			token.setUsername(username);
			String t = UUID.randomUUID().toString();
			token.setToken(t);

			GenericDAO<PasswordResetToken> tokenDAO = daoFac.getPasswordResetTokenDAO();
			tokenDAO.persist(token);
			return Response.status(OK).entity(new RestResponse(t)).build();

		} catch (Exception e) {
			logger.error("Error while generating token", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
					.build();
		}
	}

	/**
	 * Allow resetting password for a user
	 * 
	 * @param username
	 *            username
	 * @param resetToken
	 *            token for password resetting
	 * @param newPassword
	 *            new password
	 * @return Response object containing the updated user
	 */
	@POST
	@Path("/resetPassword")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response resetPassword(@FormParam("username") String username, @FormParam("resetToken") String resetToken,
			@FormParam("newPassword") String newPassword) {

		if (!isValidPassword(newPassword)) {
			return Response.status(BAD_REQUEST).entity(new RestErrorResponse(MSG_BAD_PASSWORD)).build();
		} else {
			try {
				PasswordResetToken t = new PasswordResetToken();
				t.setUsername(username);
				t.setToken(resetToken);
				GenericDAO<PasswordResetToken> tokenDAO = daoFac.getPasswordResetTokenDAO();
				PasswordResetToken token = tokenDAO.findUnique(t);
				if (token != null) {
					tokenDAO.delete(token);
				}

				if (token != null) {
					User u = new User();
					u.setUsername(username);
					HasUniqueDAO<User> userDAO = daoFac.getUserDAO();
					User user = userDAO.findUnique(u);

					// Hash a password for the first time
					String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
					user.setPassword(hashed);
					return updateEntity(user);

				} else {
					return Response.status(FORBIDDEN).entity(new RestErrorResponse("Invalid token!")).build();
				}
			} catch (Exception e) {
				logger.error("Error while retrieving entity", e);
				HibernateUtil.rollback();
				return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
						.build();
			}
		}
	}
}