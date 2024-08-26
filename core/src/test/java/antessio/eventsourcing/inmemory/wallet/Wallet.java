package antessio.eventsourcing.inmemory.wallet;

import java.math.BigDecimal;
import java.util.UUID;

import eventsourcing.aggregate.Aggregate;


public record Wallet(UUID id, BigDecimal amount, UUID ownerId) implements Aggregate<UUID> {

    public UUID getId(){
        return id;
    }

}
