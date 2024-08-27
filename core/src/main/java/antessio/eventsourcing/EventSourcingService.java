package antessio.eventsourcing;

import java.util.List;
import java.util.Optional;

import eventsourcing.Command;
import eventsourcing.Event;
import eventsourcing.EventStore;
import eventsourcing.Projector;
import eventsourcing.ProjectorStore;
import eventsourcing.aggregate.Aggregate;
import eventsourcing.aggregate.AggregateStore;

/**
 * This allows the source to publish a {@link eventsourcing.Command} that triggers an {@link Event} that eventually
 * is projected, through a {@link Projector}, on a {@link Aggregate}
 * @param <A>
 */
public class EventSourcingService<A extends Aggregate> {

    private final ReadStoreService<A> readStoreService;
    private final ProjectorStore<A> projectorStore;
    private final AggregateStore<A> aggregateStore;
    private final EventStore<A> eventStore;

    /**
     *
     * @param projectorStore Where the {@link Projector}s are stored.
     * @param aggregateStore Where the {@link Aggregate}s are stored.
     * @param eventStore Where the {@link Event}s are stored.
     */
    public EventSourcingService(ProjectorStore<A> projectorStore, AggregateStore<A> aggregateStore, EventStore<A> eventStore) {
        this.readStoreService = new ReadStoreService<>(projectorStore, aggregateStore, eventStore);
        this.projectorStore = projectorStore;
        this.aggregateStore = aggregateStore;
        this.eventStore = eventStore;
    }

    /**
     * Send a {@link Command} and returns the updated {@link Aggregate}
     * @param command
     * @return
     */
    public A publish(Command<A> command, ReadStoreService<A> readStoreService) {
        List<Event<A>> eventsToApply = command.process();

        eventStore.put(eventsToApply);
        // expected one command to update one aggregate
        return readStoreService
                .processEvents(eventsToApply)
                .getFirst();
    }

    public A publish(Command<A> command) {
        return publish(command, getReadStoreService());
    }


    /**
     * Register a {@link Projector} to handle {@link Event}
     * @param projector
     * @param <E>
     */
    public  <E extends Event<A>> void registerProjector(Projector<A, E> projector) {
        getProjectorStore().addProjector((Projector<A, Event<A>>) projector);
    }

    /**
     * Returns the {@link Aggregate} state
     * @param id
     * @return
     */
    public Optional<A> getAggregate(String id, Class<A> cls) {
        return getAggregateStore().get(id, cls);
    }


    public ReadStoreService<A> getReadStoreService() {
        return readStoreService;
    }

    public ProjectorStore<A> getProjectorStore() {
        return projectorStore;
    }

    public AggregateStore<A> getAggregateStore() {
        return aggregateStore;
    }

}
