package antessio.eventsourcing.inmemory.wallet.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import antessio.eventsourcing.inmemory.wallet.Wallet;
import eventsourcing.Event;


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
