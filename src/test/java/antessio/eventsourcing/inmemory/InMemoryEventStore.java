package antessio.eventsourcing.inmemory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import antessio.eventsourcing.Event;
import antessio.eventsourcing.EventStore;
import antessio.eventsourcing.inmemory.wallet.Wallet;


class InMemoryEventStore implements EventStore<Wallet, UUID> {

    private final List<Event<Wallet, UUID>> aggregateEvents = new ArrayList<>();
    private final List<Event<Wallet, UUID>> unprocessedEvents = new ArrayList<>();

    @Override
    public void put(List<Event<Wallet, UUID>> events) {
        unprocessedEvents.addAll(events);
    }

    @Override
    public List<Event<Wallet, UUID>> getAllEvents() {
        return List.copyOf(aggregateEvents);
    }

    @Override
    public List<Event<Wallet, UUID>> getAggregateEvents(Class<? extends Wallet> aggregateClass) {
        return aggregateEvents
                .stream()
                .filter(e -> e.getAggregateClass().equals(aggregateClass))
                .toList();
    }

    @Override
    public List<Event<Wallet, UUID>> getUnprocessedEvents() {
        return unprocessedEvents;
    }

    @Override
    public void markAsProcessed(List<Event<Wallet, UUID>> processedEvents) {
        unprocessedEvents.removeAll(processedEvents);
        aggregateEvents.addAll(processedEvents);
    }

}
