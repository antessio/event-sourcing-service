package eventsourcing;

import java.util.List;

import eventsourcing.aggregate.Aggregate;

/**
 * Generic representation of an event store.
 * It should distinguish processed from non-processed events and allow to retrieve all the events associated to an {@link Aggregate} type
 */
public interface EventStore {

    /**
     * Enqueue events to process.
     * @param events
     */
    <A extends Aggregate> void put(List<Event<A>> events);

    /**
     * All the events present
     * @return
     */
    <A extends Aggregate>List<Event<A>> getAllEvents();

    /**
     * All the events for an {@link Aggregate} type
     * @param aggregateClass
     * @return
     */
    <A extends Aggregate>List<Event<A>> getAggregateEvents(Class<? extends A> aggregateClass);

    /**
     * Get all the unprocessed events
     * @return
     */
    <A extends Aggregate>List<Event<A>> getUnprocessedEvents();

    /**
     * Mark the events as processed
     * @param processedEvents
     */
    <A extends Aggregate>void markAsProcessed(List<Event<A>> processedEvents);


}
