package antessio.eventsourcing.inmemory;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import antessio.eventsourcing.AnnotationBasedProjectorStore;
import antessio.eventsourcing.EventSourcingService;
import testutils.wallet.Wallet;
import testutils.wallet.commands.CreateWalletCommand;
import testutils.wallet.commands.TopUpWalletCommand;
import testutils.wallet.projector.WalletProjections;

class AnnotationBasedProjectorWalletTest {

    private static AnnotationBasedProjectorStore<Wallet> walletAnnotationBasedProjectorStore;
    private InMemoryAggregateStore inMemoryAggregateStore;
    private InMemoryEventStore inMemoryEventStore;

    private EventSourcingService<Wallet> eventStore;

    @BeforeAll
    static void beforeAll() {
        walletAnnotationBasedProjectorStore = new AnnotationBasedProjectorStore<>(List.of("testutils.wallet.projector"));
    }

    @BeforeEach
    void setUp() {
        inMemoryAggregateStore = new InMemoryAggregateStore();
        inMemoryEventStore = new InMemoryEventStore();
        eventStore = new EventSourcingService<>(walletAnnotationBasedProjectorStore, inMemoryAggregateStore, inMemoryEventStore);
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

        Wallet updatedWallet = eventStore.publish(new TopUpWalletCommand(wallet.id(), BigDecimal.valueOf(3000)));
        assertThat(updatedWallet)
                .isNotNull()
                .matches(w -> w.amount().intValue() == 3010);

        ;
    }

}