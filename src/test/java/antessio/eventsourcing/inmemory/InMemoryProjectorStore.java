package antessio.eventsourcing.inmemory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import antessio.eventsourcing.Event;
import antessio.eventsourcing.Projector;
import antessio.eventsourcing.ProjectorStore;
import antessio.eventsourcing.inmemory.wallet.Wallet;


class InMemoryProjectorStore implements ProjectorStore<Wallet, UUID> {

    private final Map<Class<? extends Event<Wallet>>, Projector<Wallet, Event<Wallet>>> projectorsMap = new HashMap<>();

    @Override
    public boolean hasProjector(Class<? extends Event<Wallet>> eventType) {
        return projectorsMap.containsKey(eventType);
    }

    @Override
    public void addProjector(Projector<Wallet, Event<Wallet>> projector) {
        if (projectorsMap.containsKey(projector.getSubscribedEvent())) {
            throw new IllegalArgumentException("this event has already a projector");
        }
        projectorsMap.put(projector.getSubscribedEvent(), projector);
    }

    @Override
    public Projector<Wallet, Event<Wallet>> getMatchingProjector(Class<? extends Event<Wallet>> eventType) {
        return projectorsMap.get(eventType);
    }

}
