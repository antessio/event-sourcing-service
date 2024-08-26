package antessio.eventsourcing;

public interface ProjectorStore<A extends Aggregate<ID>, ID> {

    boolean hasProjector(Class<? extends Event<A>> eventType);

    void addProjector(Projector<A, Event<A>> projector);

    Projector<A, Event<A>> getMatchingProjector(Class<? extends Event<A>> eventType);

}
