package eventsourcing.aggregate.wallet;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import eventsourcing.aggregate.Aggregate;

public record Wallet(@JsonProperty("id") UUID id, @JsonProperty("ownerId") UUID ownerId, @JsonProperty("amount") BigDecimal amount) implements Aggregate {

    @Override
    public String getId() {
        return id.toString();
    }

}
