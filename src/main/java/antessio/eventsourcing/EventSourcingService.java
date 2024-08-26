package antessio.eventsourcing;

import java.util.List;
import java.util.Optional;

/**
 * This allows the source to publish a {@link Command} that triggers an {@link Event} that eventually
 * is projected, through a {@link Projector}, on a {@link Aggregate}
 * @param <A>
 * @param <ID>
 */
public class EventSourcingService<A extends Aggregate<ID>, ID> {

    private final ReadStoreService<A, ID> readStoreService;
    private final ProjectorStore<A, ID> projectorStore;
    private final AggregateStore<A, ID> aggregateStore;


    public EventSourcingService(ProjectorStore<A, ID> projectorStore, AggregateStore<A, ID> aggregateStore, EventStore<A, ID> eventStore) {
        this.readStoreService = new ReadStoreService<>(projectorStore, aggregateStore, eventStore);
        this.projectorStore = projectorStore;
        this.aggregateStore = aggregateStore;
    }

    /**
     * Send a {@link Command} and returns the updated {@link Aggregate}
     * @param command
     * @return
     */
    public A publish(Command<A, ID> command, ReadStoreService<A, ID> readStoreService) {
        List<Event<A, ID>> eventsToApply = command.process();
        // expected one command to update one aggregate
        return readStoreService
                .processEvents(eventsToApply)
                .getFirst();
    }

    public A publish(Command<A, ID> command) {
        return publish(command, getReadStoreService());
    }


    /**
     * Register a {@link Projector} to handle {@link Event}
     * @param projector
     * @param <E>
     */
    public  <E extends Event<A, ID>> void registerProjector(Projector<A, E, ID> projector) {
        getProjectorStore().addProjector((Projector<A, Event<A, ID>, ID>) projector);
    }

    /**
     * Returns the {@link Aggregate} state
     * @param id
     * @return
     */
    public Optional<A> getAggregate(ID id) {
        return getAggregateStore().get(id);
    }


    public ReadStoreService<A, ID> getReadStoreService() {
        return readStoreService;
    }

    public ProjectorStore<A, ID> getProjectorStore() {
        return projectorStore;
    }

    public AggregateStore<A, ID> getAggregateStore() {
        return aggregateStore;
    }

}
