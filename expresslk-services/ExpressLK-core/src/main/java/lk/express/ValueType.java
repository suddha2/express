package lk.express;

import java.lang.reflect.Constructor;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ValueType", namespace = "http://express.lk")
@XmlRootElement
public enum ValueType {

	String(new Instantiator<String>(String.class)),

	Integer(new Instantiator<Integer>(Integer.class)),

	Long(new Instantiator<Long>(Long.class)),

	Float(new Instantiator<Float>(Float.class)),

	Double(new Instantiator<Double>(Double.class)),

	Boolean(new Instantiator<Boolean>(Boolean.class)),

	Date(new Instantiator<Date>(Date.class) {
		@Override
		protected Date instantiate(String value) throws Exception {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			Date greenwichTime = sdf.parse(value);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(greenwichTime);
			calendar.add(Calendar.HOUR, 5);
			calendar.add(Calendar.MINUTE, 30);
			return calendar.getTime();
		}
	}),

	DateTime(new Instantiator<Date>(Date.class) {
		@Override
		protected Date instantiate(String value) throws Exception {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			Date greenwichTime = sdf.parse(value);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(greenwichTime);
			calendar.add(Calendar.HOUR, 5);
			calendar.add(Calendar.MINUTE, 30);
			return calendar.getTime();
		}
	}),

	Time(new Instantiator<Time>(Time.class) {
		@Override
		protected Time instantiate(String value) throws Exception {
			return java.sql.Time.valueOf(value);
		}
	});

	private Instantiator<?> instantiator;

	ValueType(Instantiator<?> instantiator) {
		this.instantiator = instantiator;
	}

	public Object getObject(List<String> values) {
		try {
			if (values == null || values.isEmpty()) {
				return null;
			}
			if (values.size() == 1) {
				return instantiator.instantiate(values.get(0));
			}
			List<Object> list = new ArrayList<Object>();
			for (String value : values) {
				list.add(instantiator.instantiate(value));
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static class Instantiator<T> {

		protected Class<T> clazz;

		private Instantiator(Class<T> clazz) {
			this.clazz = clazz;
		}

		protected T instantiate(String value) throws Exception {
			Constructor<T> constructor = clazz.getConstructor(String.class);
			T t = constructor.newInstance(value);
			return t;
		}
	}
}
