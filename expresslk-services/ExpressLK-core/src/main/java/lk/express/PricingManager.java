package lk.express;

import lk.express.bean.Bus;
import lk.express.db.dao.DAOFactory;

public class PricingManager {

	protected static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

	private DiscountMarkupTaxManager markupManager;
	private DiscountMarkupTaxManager discountManager;
	private DiscountMarkupTaxManager taxManager;
	private DiscountMarkupTaxManager chargeManager;

	public PricingManager(DiscountMarkupTaxManager markupManager, DiscountMarkupTaxManager discountManager,
			DiscountMarkupTaxManager taxManager, DiscountMarkupTaxManager chargeManager) {
		this.markupManager = markupManager;
		this.discountManager = discountManager;
		this.taxManager = taxManager;
		this.chargeManager = chargeManager;
	}

	public void price(ResultWrapper result) {
		setCost(result);
		markupManager.apply(result);
		discountManager.apply(result);
		taxManager.apply(result);
		chargeManager.apply(result);
	}

	protected void setCost(ResultWrapper result) {
		Bus bus = result.getSector().getSchedule().getBus();
		if (bus.isMarkupOnly()) {
			result.setCost(0d);
			result.setChildCost(0d);
		} else {
			result.setCost(result.getFare());
			result.setChildCost(result.getChildFare());

		}
	}
}
