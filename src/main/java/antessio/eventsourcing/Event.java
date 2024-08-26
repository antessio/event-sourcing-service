package antessio.eventsourcing;

import java.time.Instant;
import java.util.UUID;

/**
 * Something that happened to an {@link Aggregate}.
 * Events eventually contribute the changing the status of the {@link Aggregate} through {@link Projector}s
 * @param <A>
 */
public interface Event <A extends Aggregate<ID>, ID>{

    UUID getEventId();

    Instant getOccurredAt();

    ID getAggregateId();
    /**
     * An event must refer to one specific {@link Aggregate}
     * @return
     */
    Class<? extends A> getAggregateClass();
}
