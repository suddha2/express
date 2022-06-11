package lk.express.db.dao.hibernate;

import lk.express.bean.Entity;
import lk.express.db.dao.HasUniqueDAO;

public abstract class HasUniqueHibernateDAO<T extends Entity> extends GenericHibernateDAO<T> implements HasUniqueDAO<T> {

}
