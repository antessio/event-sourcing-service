package eventsourcing.aggregate;

import java.util.Optional;

/**
 * Aggregate store. Any key-value implementation should work.
 * @param <A>
 *
 */
public interface AggregateStore<A extends Aggregate> {

    /**
     * Get aggregate by key
     * @param id
     * @return
     */
    Optional<A> get(String id, Class<? extends A> cls);

    /**
     * Put an aggregate into the store
     * @param a
     */
    void put(A a);

}
