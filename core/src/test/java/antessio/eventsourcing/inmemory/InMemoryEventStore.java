package antessio.eventsourcing.inmemory;

import java.util.ArrayList;
import java.util.List;

import eventsourcing.Event;
import eventsourcing.EventStore;
import testutils.wallet.Wallet;


class InMemoryEventStore implements EventStore<Wallet> {

    private final List<Event<Wallet>> aggregateEvents = new ArrayList<>();
    private final List<Event<Wallet>> unprocessedEvents = new ArrayList<>();

    @Override
    public void put(List<Event<Wallet>> events) {
        unprocessedEvents.addAll(events);
    }

    @Override
    public List<Event<Wallet>> getAllEvents() {
        return List.copyOf(aggregateEvents);
    }

    @Override
    public List<Event<Wallet>> getAggregateEvents(Class<? extends Wallet> aggregateClass) {
        return aggregateEvents
                .stream()
                .filter(e -> e.getAggregateClass().equals(aggregateClass))
                .toList();
    }

    @Override
    public List<Event<Wallet>> getUnprocessedEvents() {
        return unprocessedEvents;
    }

    @Override
    public void markAsProcessed(List<Event<Wallet>> processedEvents) {
        unprocessedEvents.removeAll(processedEvents);
        aggregateEvents.addAll(processedEvents);
    }

}
