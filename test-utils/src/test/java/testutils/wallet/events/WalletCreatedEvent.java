package testutils.wallet.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import eventsourcing.Event;
import testutils.wallet.Wallet;


public record WalletCreatedEvent(UUID eventId, UUID id, UUID ownerId, BigDecimal amount, Instant occurredAt) implements Event<Wallet> {

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String  getAggregateId() {
        return id.toString();
    }

    @Override
    public Class<? extends Wallet> getAggregateClass() {
        return Wallet.class;
    }



}
