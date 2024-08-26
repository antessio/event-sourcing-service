package antessio.eventsourcing;

import java.util.List;
import java.util.Optional;

public interface Command<A extends Aggregate<ID>, ID> {
    Optional<ID> getAggregateId();
    List<Event<A>> process();
    Class<A> getAggregateClass();
}
