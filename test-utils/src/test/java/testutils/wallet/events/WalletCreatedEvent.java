package testutils.wallet.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eventsourcing.Event;
import testutils.wallet.Wallet;


public record WalletCreatedEvent(@JsonProperty("eventId") UUID eventId, @JsonProperty("id") UUID id, @JsonProperty("ownerId") UUID ownerId,
                                 @JsonProperty("amount") BigDecimal amount, @JsonProperty("occurredAt") Instant occurredAt) implements Event<Wallet> {

    @JsonIgnore
    @Override
    public UUID getEventId() {
        return eventId;
    }

    @JsonIgnore
    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @JsonIgnore
    @Override
    public String getAggregateId() {
        return id.toString();
    }

    @Override
    public Class<? extends Wallet> getAggregateClass() {
        return Wallet.class;
    }


}
