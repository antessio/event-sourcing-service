package antessio.eventsourcing;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import eventsourcing.Event;
import eventsourcing.EventStore;
import eventsourcing.Projector;
import eventsourcing.ProjectorStore;
import eventsourcing.aggregate.Aggregate;
import eventsourcing.aggregate.AggregateStore;

/**
 * This component is used by both source and listeners. The event source use it to update an {@link Aggregate} from a {@link eventsourcing.Command} while a listener use it to
 * update one or more {@link Aggregate}s from the events.
 */
public class ReadStoreService<A extends Aggregate> {


    private final ProjectorStore<A> projectorStore;

    private final AggregateStore<A> aggregateStore;

    private final EventStore<A> eventStore;


    /**
     *
     * @param projectorStore Where the {@link Projector}s are stored.
     * @param aggregateStore Where the {@link Aggregate}s are stored.
     * @param eventStore     Where the {@link Event}s are stored.
     */
    public ReadStoreService(ProjectorStore<A> projectorStore, AggregateStore<A> aggregateStore, EventStore<A> eventStore) {
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
    public <E extends Event<A>> void registerProjector(Projector<A, E> projector) {
        getProjectorStore().addProjector((Projector<A, Event<A>>) projector);
    }

    /**
     * Returns the {@link Aggregate} state
     *
     * @param id
     *
     * @return
     */
    public Optional<A> getAggregate(String id, Class<A> cls) {
        return getAggregateStore().get(id, cls);
    }
    public List<A> processEvents() {
        List<Event<A>> unprocessedEvents = eventStore.getUnprocessedEvents();
        List<A> updatedAggregates = processEvents(unprocessedEvents);
        eventStore.markAsProcessed(unprocessedEvents);
        return updatedAggregates;
    }
    public List<A> processEvents(List<Event<A>> events) {
        // group events by aggregate
        return events.stream()
                     .collect(Collectors.groupingBy(e -> new EventKey<>(e.getAggregateId(), e.getAggregateClass())))
                     .entrySet()
                     .stream()
                     .map(entry -> {
                         Function<A, A> result = entry.getValue().stream()
                                                      .sorted(Comparator.comparing(Event::getOccurredAt))
                                                      .map(this::applyEvent)
                                                      .reduce(Function::andThen)
                                                      .orElseGet(Function::identity);
                         // apply projection to events
                         return result.apply(getAggregateStore().get(entry.getKey().aggregateId, entry.getKey().aggregateClass)
                                                                .orElse(null));


                     })
                     // store aggregate
                     .peek(getAggregateStore()::put)
                     .toList();

    }

    private record EventKey<A extends Aggregate>(String aggregateId, Class<? extends A> aggregateClass ){

    }

    private Function<A, A> applyEvent(Event<A> event) {
        return (a) -> {
            Projector<A, Event<A>> matchingProjector
                    = getProjectorStore().getMatchingProjector((Class<? extends Event<A>>) event.getClass());
            return matchingProjector.handle(a, event);
        };
    }

    public ProjectorStore<A> getProjectorStore() {
        return projectorStore;
    }

    public AggregateStore<A> getAggregateStore() {
        return aggregateStore;
    }

    public EventStore<A> getEventStore() {
        return eventStore;
    }

}
