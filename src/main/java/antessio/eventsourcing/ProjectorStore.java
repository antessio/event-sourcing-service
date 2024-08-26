package antessio.eventsourcing;

public interface ProjectorStore<A extends Aggregate<ID>, ID> {

    boolean hasProjector(Class<? extends Event<A, ID>> eventType);

    void addProjector(Projector<A, Event<A, ID>, ID> projector);

    Projector<A, Event<A, ID>, ID> getMatchingProjector(Class<? extends Event<A, ID>> eventType);

}
