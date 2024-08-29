package antessio.eventsourcing.inmemory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import eventsourcing.aggregate.Aggregate;
import eventsourcing.aggregate.AggregateStore;
import testutils.wallet.Wallet;


public class InMemoryAggregateStore implements AggregateStore {

    private final Map<String, Aggregate> aggregates = new HashMap<>();


    @Override
    public <A extends Aggregate> Optional<A> get(String id, Class<? extends A> cls) {
        return Optional.ofNullable(aggregates.get(id))
                       .filter(a -> a.getClass().isAssignableFrom(cls))
                       .map(cls::cast);
    }

    @Override
    public <A extends Aggregate> void put(A wallet) {
        aggregates.put(wallet.getId(), wallet);
    }

}
