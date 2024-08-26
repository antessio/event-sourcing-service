package antessio.eventsourcing;

/**
 * This is the entity to represent.
 * It must have a unique identifier.
 * @param <ID>
 */
public interface Aggregate<ID> {
    ID id();

}
