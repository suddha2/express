package lk.express.accounting;

public interface AccountingDelegate {

	void record(IJournalEntry entry) throws AccountingException;
}
