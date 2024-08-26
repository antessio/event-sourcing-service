package eventsourcing;

import eventsourcing.aggregate.Aggregate;

/**
 * Stores the registered {@link Projector}s.
 * @param <A>
 * @param <ID>
 */
public interface ProjectorStore<A extends Aggregate<ID>, ID> {

    boolean hasProjector(Class<? extends Event<A, ID>> eventType);

    /**
     * Add a {@link Projector}. A projector can be associated to only one {@link Event}
     * @param projector
     */
    void addProjector(Projector<A, Event<A, ID>, ID> projector);

    /**
     * Get the {@link Projector} matching the event type.
     * @param eventType
     * @return
     */
    Projector<A, Event<A, ID>, ID> getMatchingProjector(Class<? extends Event<A, ID>> eventType);

}
