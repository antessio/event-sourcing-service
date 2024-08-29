package antessio.eventsourcing.inmemory;

import java.util.HashMap;
import java.util.Map;

import eventsourcing.Event;
import eventsourcing.Projector;
import eventsourcing.ProjectorStore;
import eventsourcing.aggregate.Aggregate;


public class InMemoryProjectorStore implements ProjectorStore {

    private final Map<Class<? extends Event>, Projector> projectorsMap = new HashMap<>();

    @Override
    public <A extends Aggregate> boolean hasProjector(Class<? extends Event<A>> eventType) {
        return projectorsMap.containsKey(eventType);
    }

    @Override
    public <A extends Aggregate> void addProjector(Projector<A, Event<A>> projector) {
        if (projectorsMap.containsKey(projector.getSubscribedEvent())) {
            throw new IllegalArgumentException("this event has already a projector");
        }
        projectorsMap.put(projector.getSubscribedEvent(), projector);
    }

    @Override
    public <A extends Aggregate> Projector<A, Event<A>> getMatchingProjector(Class<? extends Event<A>> eventType) {
        return projectorsMap.get(eventType);
    }

}
