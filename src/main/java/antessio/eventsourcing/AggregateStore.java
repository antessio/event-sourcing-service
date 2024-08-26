package antessio.eventsourcing;

import java.util.Optional;

public interface AggregateStore<A extends Aggregate<ID>, ID> {

    Optional<A> get(ID id);

    void put(A a);

}
