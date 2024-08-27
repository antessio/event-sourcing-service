package testutils.wallet.commands;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import eventsourcing.Command;
import eventsourcing.Event;
import testutils.wallet.Wallet;
import testutils.wallet.events.WalletCreatedEvent;


public record CreateWalletCommand(UUID ownerId) implements Command<Wallet> {

    @Override
    public Optional<String> getAggregateId() {
        return Optional.empty();
    }

    @Override
    public List<Event<Wallet>> process() {
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
