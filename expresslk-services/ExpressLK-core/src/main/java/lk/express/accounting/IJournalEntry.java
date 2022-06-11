package lk.express.accounting;

public interface IJournalEntry {

	public static final String SUPPLIER_PREFIX = "Supplier_";

	// Expense
	public static final String CoS = "Cost of Sales"; // Or cost of goods sold

	// Liability
	public static final String VOUCHERS_OUTSTANDING = "Vouchers Outstanding";
	public static final String SALES_TAX_PAYABLE = "Sales Tax Payable";

	// Revenue
	public static final String REVENUE = "Revenue"; // Or sales
	public static final String COMMISSION_REVENUE = "Commission Revenue";
	public static final String CANC_CHARGE_REVENUE = "Cancellation Charge Revenue";

	// Asset
	public static final String CASH = "Cash";
	public static final String BANK = "Bank";
	public static final String CARD = "Card";
	public static final String mCASH = "mCash";
	public static final String eZCASH = "eZCash";
	public static final String PayPal = "PayPal";
	public static final String RECEIVABLE = "Receivable";
	public static final String WARRANT = "Warrant";
	public static final String PASS = "Pass";

	JournalEntryFragment[] getFragments();
}
