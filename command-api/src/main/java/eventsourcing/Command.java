package eventsourcing;

import java.util.List;
import java.util.Optional;

import eventsourcing.aggregate.Aggregate;

/**
 * It represents a command that change the {@link Aggregate} state
 * @param <A>
 *
 */
public interface Command<A extends Aggregate> {

    /**
     * A command must refer to an aggregate.
     * The only scenario where the aggregate id is empty is the new aggregate creation
     * @return
     */
    Optional<String> getAggregateId();

    /**
     * A command must trigger one or more events.
     * The events eventually are projected to the {@link Aggregate}
     * @return
     */
    List<Event<A>> process();

    /**
     * A command must be applied to only one specific {@link Aggregate}
     * @return
     */
    Class<A> getAggregateClass();
}
