package antessio.eventsourcing;

import java.util.Optional;

/**
 * It persists the {@link Aggregate} state
 * @param <A>
 * @param <ID>
 */
public interface AggregateStore<A extends Aggregate<ID>, ID> {

    Optional<A> get(ID id);

    void put(A a);

}
