package lk.express.db;

/**
 * This is a marker interface applicable to subclasses of
 * {@link lk.express.bean.Entity} to denote that the their instances are
 * updatable in database by SQL triggers. That is when such an entity is saved
 * to the database some of its fields may be filled or updated by triggers,
 * hence they need to be refetched.
 * 
 * Note: Timestamp columns or auto incremented columns in an
 * {@link lk.express.bean.Entity} class does not require the class to implement
 * this interface. Hibernate takes care of them natively.
 */
public interface TriggerUpdatable {
}
