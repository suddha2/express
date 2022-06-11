package lk.express.accounting;

import java.util.Date;

public class CompoundJournalEntry extends JournalEntry {

	private JournalEntryFragment[] fragments;

	public CompoundJournalEntry() {
	}

	public CompoundJournalEntry(String particulars, JournalEntryFragment... fragments) {
		super(particulars);
		this.fragments = fragments;
	}

	public CompoundJournalEntry(Date time, String particulars, JournalEntryFragment... fragments) {
		super(time, particulars);
		this.fragments = fragments;
	}

	@Override
	public JournalEntryFragment[] getFragments() {
		return fragments;
	}

	public void setFragments(JournalEntryFragment[] fragments) {
		this.fragments = fragments;
	}

	@Override
	public String toString() {
		String str = super.toString();
		for (JournalEntryFragment fragment : fragments) {
			str += "\n" + fragment;
		}
		return str;
	}
}
