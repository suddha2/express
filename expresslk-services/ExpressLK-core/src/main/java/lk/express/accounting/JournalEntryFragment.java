package lk.express.accounting;

public class JournalEntryFragment {

	public enum CrDr {
		Cr, Dr
	}

	private String ledgerFolio;
	private CrDr type;
	private double amount;
	private String description;

	public JournalEntryFragment(String ledgerFolio, CrDr type, double amount, String description) {
		this.ledgerFolio = ledgerFolio;
		this.type = type;
		this.amount = amount;
		this.description = description;
	}

	public String getLedgerFolio() {
		return ledgerFolio;
	}

	public void setLedgerFolio(String ledgerFolio) {
		this.ledgerFolio = ledgerFolio;
	}

	public CrDr getType() {
		return type;
	}

	public void setType(CrDr type) {
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "\t" + type + "\t" + amount + "\t" + ledgerFolio + (description != null ? "\t" + description : "s");
	}
}
