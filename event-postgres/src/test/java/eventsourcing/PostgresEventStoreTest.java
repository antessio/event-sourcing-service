package eventsourcing;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import antessio.eventsourcing.containers.PostgresContainer;
import jsonconversion.JacksonJsonConverter;
import testutils.wallet.Wallet;
import testutils.wallet.events.WalletCreatedEvent;
import testutils.wallet.events.WalletTopUpExecuted;
import utils.SystemUtils;

class PostgresEventStoreTest {

    private DatabaseConfiguration databaseConfiguration;
    private DatabaseInitializer databaseInitializer;
    private PostgresEventStore<Wallet> walletPostgresEventStore;

    @BeforeAll
    static void beforeAll() {
        if (SystemUtils.isTestContainerEnabled()) {
            PostgresContainer.start();
        }
    }


    @AfterAll
    static void afterAll() {
        if (SystemUtils.isTestContainerEnabled()) {
            PostgresContainer.stop();
        }
    }

    @BeforeEach
    void setUp() {

        databaseConfiguration = new DatabaseConfiguration(
                SystemUtils.getPostgresUrl(),
                "event_sourcing_user",
                "event_sourcing_password");
        databaseInitializer = new DatabaseInitializer(databaseConfiguration);
        databaseInitializer.initialize();
        walletPostgresEventStore = new PostgresEventStore<>(new JacksonJsonConverter(), databaseConfiguration);

    }

    @AfterEach
    void tearDown() {
        databaseInitializer.cleanup();
    }


    @Test
    void storeUnprocessedEvents() {
        // given
        WalletCreatedEvent walletCreatedEvent1 = new WalletCreatedEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, Instant.now());
        WalletCreatedEvent walletCreatedEvent2 = new WalletCreatedEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, Instant.now());
        walletPostgresEventStore.put(List.of(walletCreatedEvent1, walletCreatedEvent2));

        // when
        List<Event<Wallet>> unprocessedEvents = walletPostgresEventStore.getUnprocessedEvents();

        //then
        assertThat(unprocessedEvents)
                .containsOnly(walletCreatedEvent1, walletCreatedEvent2);
    }

    @Test
    void markEventsAsProcessed() {
        // given
        WalletCreatedEvent walletCreatedEvent1 = new WalletCreatedEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, Instant.now());
        WalletCreatedEvent walletCreatedEvent2 = new WalletCreatedEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, Instant.now());
        WalletTopUpExecuted wallet1TopUpExecuted = new WalletTopUpExecuted(UUID.randomUUID(), walletCreatedEvent1.id(),  BigDecimal.TEN, Instant.now());
        walletPostgresEventStore.put(List.of(walletCreatedEvent1, walletCreatedEvent2, wallet1TopUpExecuted));

        // when
        walletPostgresEventStore.markAsProcessed(List.of(walletCreatedEvent1, wallet1TopUpExecuted));

        //then
        assertThat(walletPostgresEventStore.getUnprocessedEvents())
                .containsOnly(walletCreatedEvent2);

    }

}