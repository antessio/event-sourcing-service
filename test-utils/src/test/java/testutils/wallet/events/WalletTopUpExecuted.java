package testutils.wallet.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import eventsourcing.Event;
import testutils.wallet.Wallet;


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
