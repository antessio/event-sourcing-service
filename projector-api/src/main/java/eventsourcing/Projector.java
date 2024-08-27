package eventsourcing;

import eventsourcing.aggregate.Aggregate;

/**
 * Given an existing {@link Aggregate} and an {@link Event}, applies the event to the aggregate
 * and returns the new state
 * @param <A>
 * @param <E>
 */
public interface Projector<A extends Aggregate, E extends Event<A>> {

    /**
     * Main function that applies the event to the aggregate
     * @param existingAggregate
     * @param eventPayload
     * @return
     */
    A handle(A existingAggregate, E eventPayload);

    /**
     * the type of {@link Event} this projector handles.
     * @return
     */
    Class<? extends E> getSubscribedEvent();

}
