package antessio.eventsourcing.inmemory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import antessio.eventsourcing.Event;
import antessio.eventsourcing.EventStore;
import antessio.eventsourcing.inmemory.wallet.Wallet;


class InMemoryEventStore implements EventStore<Wallet, UUID> {

    private final List<Event<Wallet>> aggregateEvents = new ArrayList<>();

    @Override
    public void put(List<Event<Wallet>> events) {
        aggregateEvents.addAll(events);
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

}
