package antessio.eventsourcing.inmemory.wallet.commands;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import antessio.eventsourcing.Command;
import antessio.eventsourcing.Event;
import antessio.eventsourcing.inmemory.wallet.Wallet;
import antessio.eventsourcing.inmemory.wallet.events.WalletTopUpExecuted;


public record TopUpWalletCommand(UUID walletId, BigDecimal amount) implements Command<Wallet, UUID> {

    @Override
    public Optional<UUID> getAggregateId() {
        return Optional.of(walletId);
    }

    @Override
    public List<Event<Wallet>> process() {
        return List.of(
                new WalletTopUpExecuted(this.walletId, this.amount)
        );
    }

    @Override
    public Class<Wallet> getAggregateClass() {
        return Wallet.class;
    }

}
