package lk.express.db.dao;

import java.util.List;

public interface GenericDAO<T> {

	T get(Integer id);

	List<T> list();

	List<T> find(T exampleInstance);

	T findUnique(T exampleInstance);

	T persist(T entity);

	void delete(T entity);
}