package lk.express.bean;

import javax.xml.bind.annotation.XmlType;

import lk.express.accounting.IJournalEntry;

@XmlType(name = "PaymentRefundMode", namespace = "http://bean.express.lk")
public enum PaymentRefundMode {

	Cash(IJournalEntry.CASH), Card(IJournalEntry.CARD), mCash(IJournalEntry.mCASH), eZCash(
			IJournalEntry.eZCASH), PayPal(IJournalEntry.PayPal), BankTransfer(IJournalEntry.BANK), Coupon(
					IJournalEntry.VOUCHERS_OUTSTANDING), Vendor(
							null), Warrant(IJournalEntry.WARRANT), Pass(IJournalEntry.PASS);

	private String accountName;

	PaymentRefundMode(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountName() {
		return accountName;
	}
}