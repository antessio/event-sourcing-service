package antessio.eventsourcing.inmemory;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import antessio.eventsourcing.AggregateStore;
import antessio.eventsourcing.EventSourcingService;
import antessio.eventsourcing.EventStore;
import antessio.eventsourcing.ProjectorStore;
import antessio.eventsourcing.inmemory.wallet.Wallet;
import antessio.eventsourcing.inmemory.wallet.commands.CreateWalletCommand;
import antessio.eventsourcing.inmemory.wallet.commands.TopUpWalletCommand;
import antessio.eventsourcing.inmemory.wallet.projector.WalletProjections;

class WalletTest {

    private WalletInMemoryEventSourcingService eventStore;

    @BeforeEach
    void setUp() {
        eventStore = new WalletInMemoryEventSourcingService();
        WalletProjections.registerProjections(eventStore);
    }

    @Test
    void shouldCreateWallet() {
        UUID ownerId = UUID.randomUUID();

        Wallet walletCreated = eventStore.publish(new CreateWalletCommand(ownerId));
        assertThat(walletCreated)
                .isNotNull()
                .matches(w -> w.getId() != null)
                .matches(w -> w.ownerId().equals(ownerId))
                .matches(w -> w.amount().equals(BigDecimal.ZERO));

    }

    @Test
    void shouldTopUpWallet() {
        Wallet wallet = new Wallet(UUID.randomUUID(), BigDecimal.TEN, UUID.randomUUID());
        eventStore.getAggregateStore().put(wallet);

        Wallet updatedWallet = eventStore.publish(new TopUpWalletCommand(wallet.getId(), BigDecimal.valueOf(3000)));
        assertThat(updatedWallet)
                .isNotNull()
                .matches(w -> w.amount().intValue() == 3010);

        ;
    }

    private static class WalletInMemoryEventSourcingService implements EventSourcingService<Wallet, UUID> {


        private InMemoryProjectorStore inMemoryProjectorStore = new InMemoryProjectorStore();
        private InMemoryAggregateStore inMemoryAggregateStore = new InMemoryAggregateStore();
        private InMemoryEventStore inMemoryEventStore = new InMemoryEventStore();

        @Override
        public ProjectorStore<Wallet, UUID> getProjectorStore() {
            return inMemoryProjectorStore;
        }

        @Override
        public AggregateStore<Wallet, UUID> getAggregateStore() {
            return inMemoryAggregateStore;
        }

        @Override
        public EventStore<Wallet, UUID> getEventStore() {
            return inMemoryEventStore;
        }

    }

}