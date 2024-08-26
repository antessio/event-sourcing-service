package eventsourcing.aggregate;


public interface Aggregate<ID> {
    ID id();

}
