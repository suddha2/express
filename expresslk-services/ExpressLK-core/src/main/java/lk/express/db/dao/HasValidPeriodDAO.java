package lk.express.db.dao;

import java.util.Date;
import java.util.List;

import lk.express.bean.HasValidPeriod;

public interface HasValidPeriodDAO<T extends HasValidPeriod> extends GenericDAO<T> {

	List<T> findValid();

	List<T> findValid(Date start, Date end);
}
