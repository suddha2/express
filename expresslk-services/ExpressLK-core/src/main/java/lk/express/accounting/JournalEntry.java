package lk.express.accounting;

import java.text.DateFormat;
import java.util.Date;

public abstract class JournalEntry implements IJournalEntry {

	protected Date time;
	protected String particulars;

	public JournalEntry() {
	}

	public JournalEntry(String particulars) {
		this(new Date(), particulars);
	}

	public JournalEntry(Date time, String particulars) {
		this.time = time;
		this.particulars = particulars;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getParticulars() {
		return particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	@Override
	public String toString() {
		DateFormat format = DateFormat.getInstance();
		return format.format(time) + "\t" + particulars;
	}
}
