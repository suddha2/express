package lk.express.accounting;

import java.util.Date;

import lk.express.accounting.JournalEntryFragment.CrDr;

public class SingleJournalEntry extends JournalEntry {

	private JournalEntryFragment[] fragments = new JournalEntryFragment[2];

	public SingleJournalEntry() {
	}

	public SingleJournalEntry(String particulars, String debitFolio, String creditFolio, double amount) {
		this(new Date(), particulars, debitFolio, creditFolio, amount);
	}

	public SingleJournalEntry(Date time, String particulars, String debitFolio, String creditFolio, double amount) {
		super(time, particulars);
		fragments[0] = new JournalEntryFragment(creditFolio, CrDr.Cr, amount, particulars);
		fragments[1] = new JournalEntryFragment(debitFolio, CrDr.Dr, amount, particulars);
	}

	@Override
	public JournalEntryFragment[] getFragments() {
		return fragments;
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
