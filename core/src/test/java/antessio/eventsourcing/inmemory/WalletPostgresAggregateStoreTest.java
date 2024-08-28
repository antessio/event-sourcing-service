package antessio.eventsourcing.inmemory;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import antessio.eventsourcing.EventSourcingService;

import antessio.eventsourcing.containers.PostgresContainer;
import eventsourcing.aggregate.AggregateStore;
import eventsourcing.aggregate.AggregateStoreDatabaseConfiguration;
import eventsourcing.aggregate.AggregateStoreDatabaseInitializer;
import eventsourcing.aggregate.PostgresAggregateStore;
import jsonconversion.JacksonJsonConverter;
import testutils.wallet.Wallet;
import testutils.wallet.commands.CreateWalletCommand;
import testutils.wallet.commands.TopUpWalletCommand;
import testutils.wallet.projector.WalletProjections;
import utils.SystemUtils;

class WalletPostgresAggregateStoreTest {

    private InMemoryProjectorStore inMemoryProjectorStore;
    private AggregateStore<Wallet> postgresAggregateStore;
    private InMemoryEventStore inMemoryEventStore;

    private EventSourcingService<Wallet> eventStore;
    private AggregateStoreDatabaseInitializer aggregateStoreDatabaseInitializer;

    @BeforeAll
    static void beforeAll() {
        if (SystemUtils.isTestContainerEnabled()){
            PostgresContainer.start();
        }
    }

    @AfterAll
    static void afterAll() {
        if (SystemUtils.isTestContainerEnabled()){
            PostgresContainer.stop();
        }
    }

    @BeforeEach
    void setUp() {
        AggregateStoreDatabaseConfiguration aggregateStoreDatabaseConfiguration = new AggregateStoreDatabaseConfiguration(
                SystemUtils.getPostgresUrl(),
                "event_sourcing_user",
                "event_sourcing_password");
        aggregateStoreDatabaseInitializer = new AggregateStoreDatabaseInitializer(aggregateStoreDatabaseConfiguration);
        aggregateStoreDatabaseInitializer.initialize();
        inMemoryProjectorStore = new InMemoryProjectorStore();
        postgresAggregateStore = new PostgresAggregateStore<>(new JacksonJsonConverter(), aggregateStoreDatabaseConfiguration);
        inMemoryEventStore = new InMemoryEventStore();
        eventStore = new EventSourcingService<>(inMemoryProjectorStore, postgresAggregateStore, inMemoryEventStore);

        WalletProjections.getProjectors().forEach(eventStore::registerProjector);
    }

    @AfterEach
    void tearDown() {
        aggregateStoreDatabaseInitializer.cleanup();
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