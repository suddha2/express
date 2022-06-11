package lk.express;

import java.math.BigInteger;

import lk.express.admin.Division;
import lk.express.admin.User;
import lk.express.bean.HasAllowedDivisions;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.HasUniqueDAO;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import lk.express.bean.Entity;

public class SessionData {

	private String sessionId;
	private String locale;
	private String username;

	private User user;
	private BigInteger divisionBitmask;
	private BigInteger visibleDivisionBitmask;

	public SessionData empty() {
		sessionId = null;
		locale = null;
		username = null;

		user = null;
		divisionBitmask = null;
		visibleDivisionBitmask = null;

		return this;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public User getUser() {
		if (user == null) {
			if (username != null && !username.isEmpty()) {
				DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);
				HasUniqueDAO<User> userDAO = daoFac.getUserDAO();
				user = userDAO.findUnique(new User(username));
			}
		}
		return user;
	}

	public BigInteger getDivisionBitmask() {
		if (divisionBitmask == null) {
			User user = getUser();
			if (user != null) {
				divisionBitmask = user.getDivisionsBitmask();
			}
		}
		return divisionBitmask;
	}

	public BigInteger getVisibleDivisionsBitmask() {
		if (visibleDivisionBitmask == null) {
			if (User.SYS_USER.equals(username)) {
				visibleDivisionBitmask = new BigInteger("2").pow(HasAllowedDivisions.BITMASK_LENGTH).subtract(
						BigInteger.ONE);
			} else {
				User user = getUser();
				if (user != null) {
					visibleDivisionBitmask = user.getVisibleDivisionsBitmask();
				}
			}
		}
		return visibleDivisionBitmask;
	}

	public Criterion getAllowedDivisionsCriterion() {
		BigInteger visibleDivisionsBitmask = getVisibleDivisionsBitmask();

		BigInteger allDivs = new BigInteger("2").pow(Division.getDivisionCount()).subtract(BigInteger.ONE);
		if (visibleDivisionsBitmask.and(allDivs).equals(allDivs)) {
			return null;
		}

//		BigInteger oneBitValue = getOneBitValue(visibleDivisionsBitmask);
//		if (oneBitValue != null) {
//			return Restrictions.or(Restrictions.eq("allowedDivisions", oneBitValue),
//					Restrictions.eq("allowedDivisions", oneBitValue.add(BigInteger.ONE)));
//		}
//
//		BigInteger zeroBitValue = getZeroBitValue(visibleDivisionsBitmask);
//		if (zeroBitValue != null) {
//			return Restrictions.and(Restrictions.ne("allowedDivisions", zeroBitValue),
//					Restrictions.ne("allowedDivisions", BigInteger.ZERO));
//		}

		return Restrictions.sqlRestriction("{alias}.allowed_divisions & ? > 0",
				new BigInteger[] { visibleDivisionsBitmask }, new Type[] { StandardBasicTypes.BIG_INTEGER });
	}

	public String getAllowedDivisionsWhereClause(String prefix, boolean isHql) {
		BigInteger visibleDivisionsBitmask = getVisibleDivisionsBitmask();

		BigInteger allDivs = new BigInteger("2").pow(Division.getDivisionCount()).subtract(BigInteger.ONE);
		if (visibleDivisionsBitmask.and(allDivs).equals(allDivs)) {
			return "TRUE";
		}

		String field = (prefix != null && !prefix.isEmpty() ? prefix + "." : "")
				+ (isHql ? "allowedDivisions" : "allowed_divisions");
		
//		BigInteger oneBitValue = getOneBitValue(visibleDivisionsBitmask);
//		if (oneBitValue != null) {
//			return "(" + field + " = " + printBigInteger(oneBitValue) + " OR " + field + " = "
//					+ printBigInteger(oneBitValue.add(BigInteger.ONE)) + ")";
//		}
//
//		BigInteger zeroBitValue = getZeroBitValue(visibleDivisionsBitmask);
//		if (zeroBitValue != null) {
//			return "(" + field + " != " + printBigInteger(zeroBitValue) + " AND " + field + " != 0)";
//		}

		return field + " & " + printBigInteger(visibleDivisionsBitmask) + " > 0";
	}

	private String printBigInteger(BigInteger val) {
		return val.toString();
	}

//	private BigInteger getOneBitValue(BigInteger visibleDivisionsBitmask) {
//		// if n & (n - 1) == 0, then n is a power of 2
//		if (visibleDivisionsBitmask.and(visibleDivisionsBitmask.subtract(BigInteger.ONE)).equals(BigInteger.ZERO)) {
//			return visibleDivisionsBitmask;
//		}
//		return null;
//	}

//	private BigInteger getZeroBitValue(BigInteger visibleDivisionsBitmask) {
//		BigInteger allDivs = new BigInteger("2").pow(Division.getDivisionCount()).subtract(BigInteger.ONE);
//		BigInteger diff = allDivs.subtract(visibleDivisionsBitmask);
//		// if n & (n - 1) == 0, then n is a power of 2
//		if (diff.and(diff.subtract(BigInteger.ONE)).equals(BigInteger.ZERO)) {
//			return diff;
//		}
//		return null;
//	}
}
