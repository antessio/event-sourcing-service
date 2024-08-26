package antessio.eventsourcing.inmemory.wallet.events;

import java.math.BigDecimal;
import java.util.UUID;

import antessio.eventsourcing.Event;
import antessio.eventsourcing.inmemory.wallet.Wallet;


public record WalletTopUpExecuted(UUID walletId, BigDecimal amount) implements Event<Wallet> {


    @Override
    public Class<? extends Wallet> getAggregateClass() {
        return Wallet.class;
    }

}
