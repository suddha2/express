package lk.express.accounting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingDelegate implements AccountingDelegate {

	private static final Logger accountingLogger = LoggerFactory.getLogger("accountingLogger");

	@Override
	public void record(IJournalEntry entry) throws AccountingException {
		accountingLogger.info(entry.toString());
	}
}
