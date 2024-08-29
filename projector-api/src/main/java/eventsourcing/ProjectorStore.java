package eventsourcing;

import eventsourcing.aggregate.Aggregate;

/**
 * Stores the registered {@link Projector}s.
 */
public interface ProjectorStore {

    <A extends Aggregate> boolean hasProjector(Class<? extends Event<A>> eventType);

    /**
     * Add a {@link Projector}. A projector can be associated to only one {@link Event}
     * @param projector
     */
    <A extends Aggregate> void addProjector(Projector<A, Event<A>> projector);

    /**
     * Get the {@link Projector} matching the event type.
     * @param eventType
     * @return
     */
    <A extends Aggregate> Projector<A, Event<A>> getMatchingProjector(Class<? extends Event<A>> eventType);

}
