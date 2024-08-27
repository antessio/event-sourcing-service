package antessio.eventsourcing.inmemory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import antessio.eventsourcing.inmemory.wallet.Wallet;
import eventsourcing.aggregate.AggregateStore;


class InMemoryAggregateStore implements AggregateStore<Wallet> {

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
