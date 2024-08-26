package antessio.eventsourcing.inmemory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import antessio.eventsourcing.inmemory.wallet.Wallet;
import eventsourcing.aggregate.AggregateStore;


class InMemoryAggregateStore implements AggregateStore<Wallet, UUID> {

    private final Map<UUID, Wallet> aggregates = new HashMap<>();

    @Override
    public Optional<Wallet> get(UUID uuid) {
        return Optional.ofNullable(aggregates.get(uuid));
    }

    @Override
    public void put(Wallet wallet) {
        aggregates.put(wallet.getId(), wallet);
    }

}
