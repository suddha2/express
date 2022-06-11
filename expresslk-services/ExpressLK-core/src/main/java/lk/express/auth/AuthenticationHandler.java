package lk.express.auth;

import java.util.Date;

import lk.express.CRUD;
import lk.express.admin.User;
import lk.express.api.APIToken;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;
import lk.express.db.dao.HasUniqueDAO;

public abstract class AuthenticationHandler {

	public static final String USERNAME = "username";
	public static final String TOKEN = "token";

	protected static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

	public String authenticate(String username, String token) {

		if (username == null || username.trim().isEmpty() || token == null || token.trim().isEmpty()) {
			// anonymous user
			return null;
		}

		APIToken t = new APIToken();
		t.setUsername(username);
		t.setToken(token);
		GenericDAO<APIToken> tokenDAO = daoFac.getGenericDAO(APIToken.class);

		t = tokenDAO.findUnique(t);
		if (t != null && t.getExpiry().after(new Date())) {
			return username;
		}

		// anonymous user
		return null;
	}

	/**
	 * SOAP method authorization
	 * 
	 * @param username
	 * @param methodName
	 * @return
	 */
	public boolean authorize(String username, String methodName) {
		// FIXME implements WS method authorization
		if (username != null) {
			return true;
		}
		return false;
	}

	/**
	 * REST API method authorization
	 * 
	 * @param username
	 * @param serviceName
	 * @param methodName
	 * @return
	 */
	public boolean authorize(String username, String serviceName, String methodName) {
		if (username != null) {
			HasUniqueDAO<User> userDAO = daoFac.getUserDAO();
			User user = userDAO.findUnique(new User(username));
			String permissionCode = serviceName + "/" + methodName;
			return user.hasPermission(permissionCode);
		}
		return false;
	}

	/**
	 * REST bean authorization
	 * 
	 * @param username
	 * @param beanClass
	 * @param operation
	 * @return
	 */
	public boolean authorize(String username, String beanClass, CRUD operation) {
		if (username != null) {
			HasUniqueDAO<User> userDAO = daoFac.getUserDAO();
			User user = userDAO.findUnique(new User(username));
			String className = beanClass.substring(beanClass.lastIndexOf(".") + 1);
			if (className.endsWith("Light")) {
				className = className.substring(0, className.length() - 5);
			}
			String permissionCode = className.toLowerCase() + "/" + operation.getPermission();
			return user.hasPermission(permissionCode);
		}
		return false;
	}
}
