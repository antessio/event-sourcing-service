package eventsourcing;

import java.time.Instant;
import java.util.UUID;

import eventsourcing.aggregate.Aggregate;

/**
 * Something that happened to an {@link Aggregate}.
 * Events eventually contribute the changing the status of the {@link Aggregate} through projectors
 * @param <A>
 */
public interface Event <A extends Aggregate>{

    UUID getEventId();

    Instant getOccurredAt();

    String getAggregateId();
    /**
     * An event must refer to one specific {@link Aggregate}
     * @return
     */
    Class<? extends A> getAggregateClass();
}
