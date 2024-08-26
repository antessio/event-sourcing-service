package antessio.eventsourcing;

public interface Event <A extends Aggregate<?>>{

    Class<? extends A> getAggregateClass();
}
