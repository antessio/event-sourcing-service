package testutils.wallet;

import java.math.BigDecimal;
import java.util.UUID;

import eventsourcing.aggregate.Aggregate;


public record Wallet(UUID id, BigDecimal amount, UUID ownerId) implements Aggregate {


    @Override
    public String getId() {
        return id.toString();
    }

}
