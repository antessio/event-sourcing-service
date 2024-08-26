package antessio.eventsourcing;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This component is used by both source and listeners. The event source use it to update an {@link Aggregate} from a {@link Command} while a listener use it to
 * update one or more {@link Aggregate}s from the events.
 */
public class ReadStoreService<A extends Aggregate<ID>, ID> {


    private final ProjectorStore<A, ID> projectorStore;

    private final AggregateStore<A, ID> aggregateStore;

    private final EventStore<A, ID> eventStore;


    /**
     *
     * @param projectorStore Where the {@link Projector}s are stored.
     * @param aggregateStore Where the {@link Aggregate}s are stored.
     * @param eventStore     Where the {@link Event}s are stored.
     */
    public ReadStoreService(ProjectorStore<A, ID> projectorStore, AggregateStore<A, ID> aggregateStore, EventStore<A, ID> eventStore) {
        this.projectorStore = projectorStore;
        this.aggregateStore = aggregateStore;
        this.eventStore = eventStore;
    }


    /**
     * Register a {@link Projector} to handle {@link Event}
     *
     * @param projector
     * @param <E>
     */
    public <E extends Event<A, ID>> void registerProjector(Projector<A, E, ID> projector) {
        getProjectorStore().addProjector((Projector<A, Event<A, ID>, ID>) projector);
    }

    /**
     * Returns the {@link Aggregate} state
     *
     * @param id
     *
     * @return
     */
    public Optional<A> getAggregate(ID id) {
        return getAggregateStore().get(id);
    }
    public List<A> processEvents() {
        List<Event<A, ID>> unprocessedEvents = eventStore.getUnprocessedEvents();
        List<A> updatedAggregates = processEvents(unprocessedEvents);
        eventStore.markAsProcessed(unprocessedEvents);
        return updatedAggregates;
    }
    public List<A> processEvents(List<Event<A, ID>> events) {
        // group events by aggregate
        return events.stream()
                     .collect(Collectors.groupingBy(Event::getAggregateId))
                     .entrySet()
                     .stream()
                     .map(entry -> {
                         Function<A, A> result = entry.getValue().stream()
                                                      .sorted(Comparator.comparing(Event::getOccurredAt))
                                                      .map(this::applyEvent)
                                                      .reduce(Function::andThen)
                                                      .orElseGet(Function::identity);
                         // apply projection to events
                         return result.apply(getAggregateStore().get(entry.getKey())
                                                                .orElse(null));


                     })
                     // store aggregate
                     .peek(getAggregateStore()::put)
                     .toList();

    }

    private Function<A, A> applyEvent(Event<A, ID> event) {
        return (a) -> {
            Projector<A, Event<A, ID>, ID> matchingProjector
                    = getProjectorStore().getMatchingProjector((Class<? extends Event<A, ID>>) event.getClass());
            return matchingProjector.handle(a, event);
        };
    }

    public ProjectorStore<A, ID> getProjectorStore() {
        return projectorStore;
    }

    public AggregateStore<A, ID> getAggregateStore() {
        return aggregateStore;
    }

    public EventStore<A, ID> getEventStore() {
        return eventStore;
    }

}
