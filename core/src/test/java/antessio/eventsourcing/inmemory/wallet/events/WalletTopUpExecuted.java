package antessio.eventsourcing.inmemory.wallet.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import antessio.eventsourcing.inmemory.wallet.Wallet;
import eventsourcing.Event;


public record WalletTopUpExecuted(UUID eventId, UUID walletId, BigDecimal amount, Instant occurredAt) implements Event<Wallet> {


    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getAggregateId() {
        return walletId.toString();
    }

    @Override
    public Class<? extends Wallet> getAggregateClass() {
        return Wallet.class;
    }

}
