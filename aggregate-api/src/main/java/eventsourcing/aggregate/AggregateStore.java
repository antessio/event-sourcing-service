package eventsourcing.aggregate;

import java.util.Optional;

/**
 * Aggregate store. Any key-value implementation should work.
 * @param <A>
 * @param <ID>
 */
public interface AggregateStore<A extends Aggregate<ID>, ID> {

    /**
     * Get aggregate by key
     * @param id
     * @return
     */
    Optional<A> get(ID id);

    /**
     * Put an aggregate into the store
     * @param a
     */
    void put(A a);

}
