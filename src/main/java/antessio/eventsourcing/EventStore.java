package antessio.eventsourcing;

import java.util.List;

public interface EventStore<A extends Aggregate<ID>, ID> {

    void put(List<Event<A, ID>> events);
    List<Event<A, ID>> getAllEvents();
    List<Event<A, ID>> getAggregateEvents(Class<? extends A> aggregateClass);
    List<Event<A, ID>> getUnprocessedEvents();
    void markAsProcessed(List<Event<A, ID>> processedEvents);


}
