package antessio.eventsourcing;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface EventSourcingService<A extends Aggregate<ID>, ID> {

    ProjectorStore<A, ID> getProjectorStore();

    AggregateStore<A, ID> getAggregateStore();

    EventStore<A, ID> getEventStore();

    default A publish(Command<A, ID> command) {
        Optional<A> maybeAggregate = command.getAggregateId()
                                            .flatMap(aggregateId -> getAggregateStore().get(aggregateId));
        List<Event<A>> eventsToApply = command.process();
        Function<A, A> result = eventsToApply.stream()
                                             .map(this::applyEvent)
                                             .reduce(Function::compose)
                                             .orElseGet(Function::identity);
        A updatedAggregate = result.apply(maybeAggregate.orElse(null));
        getEventStore().put(eventsToApply);
        getAggregateStore().put(updatedAggregate);
        return updatedAggregate;
    }


    default <E extends Event<A>> void registerProjector(Projector<A, E> projector) {
        getProjectorStore().addProjector((Projector<A, Event<A>>) projector);
    }

    default Optional<A> getAggregate(ID id) {
        return getAggregateStore().get(id);
    }

    private Function<A, A> applyEvent(Event<A> event) {
        return (a) -> {
            Projector<A, Event<A>> matchingProjector = getProjectorStore().getMatchingProjector((Class<? extends Event<A>>) event.getClass());
            return matchingProjector.handle(a, event);
        };
    }

}
