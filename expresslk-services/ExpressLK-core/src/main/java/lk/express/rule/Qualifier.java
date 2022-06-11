package lk.express.rule;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.DiscountCode;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;
import lk.express.io.file.FileManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlType(name = "Qualifier", namespace = "http://rule.express.lk")
@XmlRootElement
public enum Qualifier {

	Equals("=", new Evaluator() {

		@Override
		public boolean evaluate(Object arg1, Object arg2) {
			return arg1.equals(arg2);
		}
	}),

	NotEquals("!=", new Evaluator() {

		@Override
		public boolean evaluate(Object arg1, Object arg2) {
			return !arg1.equals(arg2);
		}
	}),

	LessThan("<", new NumericalEvaluator() {

		@Override
		protected boolean evaluateNumerical(Number arg1, Number arg2) {
			return arg1.doubleValue() < arg2.doubleValue();
		}
	}),

	GreaterThan(">", new NumericalEvaluator() {

		@Override
		protected boolean evaluateNumerical(Number arg1, Number arg2) {
			return arg1.doubleValue() > arg2.doubleValue();
		}
	}),

	LessThanOrEquals("<=", new NumericalEvaluator() {

		@Override
		protected boolean evaluateNumerical(Number arg1, Number arg2) {
			return arg1.doubleValue() <= arg2.doubleValue();
		}
	}),

	GreaterThanOrEquals(">=", new NumericalEvaluator() {

		@Override
		protected boolean evaluateNumerical(Number arg1, Number arg2) {
			return arg1.doubleValue() >= arg2.doubleValue();
		}
	}),

	Like("LIKE", new StringEvaluator() {

		@Override
		protected boolean evaluateString(String arg1, String arg2) {
			return arg1.toString().equalsIgnoreCase(arg2.toString());
		}
	}),

	BeforeDateTime("BEFORE_DATETIME", new DateTimeEvaluator() {

		@Override
		protected boolean evaluateDateTime(Date arg1, Date arg2) {
			return arg1.before(arg2);
		}
	}),

	AfterDateTime("AFTER_DATETIME", new DateTimeEvaluator() {

		@Override
		protected boolean evaluateDateTime(Date arg1, Date arg2) {
			return arg1.after(arg2);
		}
	}),

	BeforeTime("BEFORE_TIME", new TimeEvaluator() {

		@Override
		protected boolean evaluateTime(Time arg1, Time arg2) {
			return arg1.before(arg2);
		}
	}),

	AfterTime("AFTER_TIME", new TimeEvaluator() {

		@Override
		protected boolean evaluateTime(Time arg1, Time arg2) {
			return arg1.after(arg2);
		}
	}),

	Is("IS", new BooleanEvaluator() {

		@Override
		protected boolean evaluateBoolean(Boolean arg1, Boolean arg2) {
			return arg1.equals(arg2);
		}
	}),

	IsNot("IS_NOT", new BooleanEvaluator() {

		@Override
		protected boolean evaluateBoolean(Boolean arg1, Boolean arg2) {
			return !arg1.equals(arg2);
		}
	}),

	Contains("CONTAINS", new CollectionEvaluator() {

		@Override
		protected boolean evaluateCollection(Collection<String> arg1, String arg2) {
			return arg1.contains(arg2);
		}
	}),

	NotContains("NOT_CONTAINS", new CollectionEvaluator() {

		@Override
		protected boolean evaluateCollection(Collection<String> arg1, String arg2) {
			return !arg1.contains(arg2);
		}
	}),

	IPIsFrom("IP_IS_FROM", new IPEvaluator() {

		@Override
		protected boolean evaluateIP(String ip, String country) {
			logger.debug("Checking whether IP " + ip + " is from " + country);
			List<IPBlock> blocks = IP_BLOCKS.get(country);
			if (blocks != null) {
				for (IPBlock block : blocks) {
					if (block.contains(ip)) {
						logger.debug("IP found in " + block);
						return true;
					}
				}
			}
			return false;
		}
	}),

	IPIsNotFrom("IP_IS_NOT_FROM", new IPEvaluator() {

		@Override
		protected boolean evaluateIP(String ip, String country) {
			logger.debug("Checking whether IP " + ip + " is not from " + country);
			List<IPBlock> blocks = IP_BLOCKS.get(country);
			if (blocks != null) {
				for (IPBlock block : blocks) {
					if (block.contains(ip)) {
						logger.debug("IP found in " + block);
						return false;
					}
				}
			}
			List<IPBlock> reserved = IP_BLOCKS.get(IP_RESERVED);
			for (IPBlock block : reserved) {
				if (block.contains(ip)) {
					logger.debug("IP found in " + block);
					return false;
				}
			}
			return true;
		}
	}),

	DiscountScheme("DISCOUNT_SCHEME", new StringEvaluator() {

		@Override
		protected boolean evaluateString(String code, String schemeCode) {
			DiscountCode ex = new DiscountCode();
			ex.setCode(code);
			ex.setSchemeCode(schemeCode);
			ex.setActive(true);

			DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);
			GenericDAO<DiscountCode> dao = daoFac.getDiscountCodeDAO();
			return dao.findUnique(ex) != null;
		}
	});

	private String symbol;
	private Evaluator evaluator;

	Qualifier(String symbol, Evaluator evaluator) {
		this.symbol = symbol;
		this.evaluator = evaluator;
	}

	public String getSymbol() {
		return symbol;
	}

	@Override
	public String toString() {
		return symbol;
	}

	public static Qualifier bySymbol(String symbol) {
		for (Qualifier q : values()) {
			if (q.symbol.equals(symbol)) {
				return q;
			}
		}
		return null;
	}

	public boolean evaluate(Object arg1, Object arg2) {
		return evaluator.evaluate(arg1, arg2);
	}

	private interface Evaluator {
		public boolean evaluate(Object arg1, Object arg2);
	}

	private static abstract class NumericalEvaluator implements Evaluator {

		@Override
		public boolean evaluate(Object arg1, Object arg2) {
			if (arg1 instanceof Number && arg2 instanceof Number) {
				return evaluateNumerical((Number) arg1, (Number) arg2);
			}
			return false;
		}

		protected abstract boolean evaluateNumerical(Number arg1, Number arg2);
	}

	private static abstract class StringEvaluator implements Evaluator {

		@Override
		public boolean evaluate(Object arg1, Object arg2) {
			if (arg1 instanceof String && arg2 instanceof String) {
				return evaluateString((String) arg1, (String) arg2);
			}
			return false;
		}

		protected abstract boolean evaluateString(String arg1, String arg2);
	}

	private static abstract class CollectionEvaluator implements Evaluator {

		@SuppressWarnings("unchecked")
		@Override
		public boolean evaluate(Object arg1, Object arg2) {
			if (arg1 instanceof Collection && arg2 instanceof String) {
				return evaluateCollection((Collection<String>) arg1, (String) arg2);
			}
			return false;
		}

		protected abstract boolean evaluateCollection(Collection<String> arg1, String arg2);
	}

	private static abstract class DateTimeEvaluator implements Evaluator {

		@Override
		public boolean evaluate(Object arg1, Object arg2) {
			if (arg1 instanceof Date && arg2 instanceof Date) {
				return evaluateDateTime((Date) arg1, (Date) arg2);
			}
			return false;
		}

		protected abstract boolean evaluateDateTime(Date arg1, Date arg2);

	}

	private static abstract class TimeEvaluator implements Evaluator {

		@Override
		public boolean evaluate(Object arg1, Object arg2) {
			if (arg1 instanceof Time && arg2 instanceof Time) {
				return evaluateTime((Time) arg1, (Time) arg2);
			}
			return false;
		}

		protected abstract boolean evaluateTime(Time arg1, Time arg2);

	}

	private static abstract class BooleanEvaluator implements Evaluator {

		@Override
		public boolean evaluate(Object arg1, Object arg2) {
			if (arg1 instanceof Boolean && arg2 instanceof Boolean) {
				return evaluateBoolean((Boolean) arg1, (Boolean) arg2);
			}
			return false;
		}

		protected abstract boolean evaluateBoolean(Boolean arg1, Boolean arg2);
	}

	private static abstract class IPEvaluator implements Evaluator {

		protected static final Logger logger = LoggerFactory.getLogger(IPEvaluator.class);

		protected static Map<String, List<IPBlock>> IP_BLOCKS = new HashMap<String, List<IPBlock>>();
		public static final String IP_RESERVED = "RESERVED";

		static {
			JsonReader jReader = null;
			try {
				InputStream stream = FileManager.readFile("ip.json");
				jReader = Json.createReader(new BufferedReader(new InputStreamReader(stream)));
				JsonObject map = jReader.readObject();
				for (Entry<String, JsonValue> entry : map.entrySet()) {
					JsonArray ranges = (JsonArray) entry.getValue();
					List<IPBlock> blocks = new ArrayList<IPBlock>();
					for (JsonValue o : ranges) {
						JsonArray range = (JsonArray) o;
						String lo = ((JsonString) range.get(0)).getString();
						String hi = ((JsonString) range.get(1)).getString();
						blocks.add(new IPBlock(lo, hi));
					}
					IP_BLOCKS.put(entry.getKey(), blocks);
				}
			} catch (Exception e) {
				logger.error("Exception while reading the IP block file", e);
				throw new RuntimeException(e);
			} finally {
				if (jReader != null) {
					jReader.close();
				}
			}
		}

		@Override
		public boolean evaluate(Object arg1, Object arg2) {
			if (arg1 instanceof String && arg2 instanceof String) {
				return evaluateIP((String) arg1, (String) arg2);
			}
			return false;
		}

		protected abstract boolean evaluateIP(String ip, String country);
	}

	private static class IPBlock {

		private long ipLo;
		private long ipHi;

		public IPBlock(String lo, String hi) throws UnknownHostException {
			ipLo = ipToLong(InetAddress.getByName(lo));
			ipHi = ipToLong(InetAddress.getByName(hi));
		}

		public boolean contains(String ip) {
			try {
				long ipToTest = ipToLong(InetAddress.getByName(ip));
				return ipToTest >= ipLo && ipToTest <= ipHi;
			} catch (UnknownHostException e) {
				return false;
			}
		}

		private static long ipToLong(InetAddress ip) {
			byte[] octets = ip.getAddress();
			long result = 0;
			for (byte octet : octets) {
				result <<= 8;
				result |= octet & 0xff;
			}
			return result;
		}

		@Override
		public String toString() {
			return ipLo + " - " + ipHi;
		}
	}
}