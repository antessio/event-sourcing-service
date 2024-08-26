package antessio.eventsourcing;

public interface Projector<A extends Aggregate<?>, E extends Event<A>> {

    A handle(A existingAggregate, E eventPayload);
    Class<? extends E> getSubscribedEvent();

}
