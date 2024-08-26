package antessio.eventsourcing.inmemory.wallet.commands;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import antessio.eventsourcing.Command;
import antessio.eventsourcing.inmemory.wallet.Wallet;
import antessio.eventsourcing.inmemory.wallet.events.WalletCreatedEvent;
import eventsourcing.Event;


public record CreateWalletCommand(UUID ownerId) implements Command<Wallet, UUID> {

    @Override
    public Optional<UUID> getAggregateId() {
        return Optional.empty();
    }

    @Override
    public List<Event<Wallet, UUID>> process() {
        return List.of(new WalletCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                this.ownerId,
                BigDecimal.ZERO,
                Instant.now()
        ));
    }

    @Override
    public Class<Wallet> getAggregateClass() {
        return Wallet.class;
    }

}
