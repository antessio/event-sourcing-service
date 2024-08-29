package antessio.eventsourcing.inmemory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import eventsourcing.Event;
import eventsourcing.EventStore;
import eventsourcing.aggregate.Aggregate;


public class InMemoryEventStore implements EventStore {

    private final List<Event<? extends Aggregate>> aggregateEvents = new ArrayList<>();
    private final List<Event<? extends Aggregate>> unprocessedEvents = new ArrayList<>();


    @Override
    public <A extends Aggregate> void put(List<Event<A>> events) {
        unprocessedEvents.addAll(events);
    }

    @Override
    public <A extends Aggregate> List<Event<A>> getAllEvents() {
        return Stream.concat(
                             unprocessedEvents.stream(),
                             aggregateEvents.stream())
                     .map(e -> (Event<A>) e)
                     .toList();
    }

    @Override
    public <A extends Aggregate> List<Event<A>> getAggregateEvents(Class<? extends A> aggregateClass) {
        return aggregateEvents
                .stream()
                .filter(e -> e.getAggregateClass().equals(aggregateClass))
                .map(e -> (Event<A>) e)
                .toList();
    }

    @Override
    public <A extends Aggregate> List<Event<A>> getUnprocessedEvents() {
        return unprocessedEvents
                .stream()
                .map(e -> (Event<A>) e)
                .toList();
    }

    @Override
    public <A extends Aggregate> void markAsProcessed(List<Event<A>> processedEvents) {
        unprocessedEvents.removeAll(processedEvents);
        aggregateEvents.addAll(processedEvents);
    }

}
