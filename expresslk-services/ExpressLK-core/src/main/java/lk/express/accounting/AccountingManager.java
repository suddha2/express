package lk.express.accounting;

public class AccountingManager {

	private static AccountingManager INSTANCE = new AccountingManager();

	public static AccountingManager getInstance() {
		return INSTANCE;
	}

	private AccountingDelegate delegate;

	private AccountingManager() {
		delegate = new LoggingDelegate();
	}

	public void record(IJournalEntry entry) throws AccountingException {
		delegate.record(entry);
	}
}
