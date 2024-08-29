package eventsourcing.aggregate;

import java.util.Optional;

/**
 * Aggregate store. Any key-value implementation should work.
 *
 */
public interface AggregateStore {

    /**
     * Get aggregate by key
     * @param id
     * @return
     */
    <A extends Aggregate>Optional<A> get(String id, Class<? extends A> cls);

    /**
     * Put an aggregate into the store
     * @param a
     */
    <A extends Aggregate> void put(A a);

}
