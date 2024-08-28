package antessio.eventsourcing.inmemory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import eventsourcing.aggregate.AggregateStore;
import testutils.wallet.Wallet;


public class InMemoryAggregateStore implements AggregateStore<Wallet> {

    private final Map<String, Wallet> aggregates = new HashMap<>();


    @Override
    public Optional<Wallet> get(String id, Class<? extends Wallet> cls) {
        return Optional.ofNullable(aggregates.get(id))
                       .filter(a -> a.getClass().isAssignableFrom(cls));
    }

    @Override
    public void put(Wallet wallet) {
        aggregates.put(wallet.getId(), wallet);
    }

}
