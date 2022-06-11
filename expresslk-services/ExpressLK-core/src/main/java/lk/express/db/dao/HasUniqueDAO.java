package lk.express.db.dao;

public interface HasUniqueDAO<T> extends GenericDAO<T> {

	T getUnique(Object unique);
}
