package antessio.eventsourcing.inmemory.wallet.commands;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import antessio.eventsourcing.Command;
import antessio.eventsourcing.Event;
import antessio.eventsourcing.inmemory.wallet.Wallet;
import antessio.eventsourcing.inmemory.wallet.events.WalletCreatedEvent;


public record CreateWalletCommand(UUID ownerId) implements Command<Wallet, UUID> {

    @Override
    public Optional<UUID> getAggregateId() {
        return Optional.empty();
    }

    @Override
    public List<Event<Wallet>> process() {
        return List.of(new WalletCreatedEvent(
                UUID.randomUUID(),
                this.ownerId,
                BigDecimal.ZERO
        ));
    }

    @Override
    public Class<Wallet> getAggregateClass() {
        return Wallet.class;
    }

}
