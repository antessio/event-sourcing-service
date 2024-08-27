package antessio.eventsourcing.inmemory.wallet.commands;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import antessio.eventsourcing.Command;
import antessio.eventsourcing.inmemory.wallet.Wallet;
import antessio.eventsourcing.inmemory.wallet.events.WalletTopUpExecuted;
import eventsourcing.Event;


public record TopUpWalletCommand(UUID walletId, BigDecimal amount) implements Command<Wallet> {

    @Override
    public Optional<String> getAggregateId() {
        return Optional.of(walletId).map(UUID::toString);
    }

    @Override
    public List<Event<Wallet>> process() {
        return List.of(
                new WalletTopUpExecuted(UUID.randomUUID(), this.walletId, this.amount, Instant.now())
        );
    }

    @Override
    public Class<Wallet> getAggregateClass() {
        return Wallet.class;
    }

}
