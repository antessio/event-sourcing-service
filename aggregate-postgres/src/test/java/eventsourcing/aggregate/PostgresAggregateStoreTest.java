package eventsourcing.aggregate;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import antessio.eventsourcing.containers.PostgresContainer;
import eventsourcing.aggregate.wallet.Wallet;
import jsonconversion.JacksonJsonConverter;
import utils.SystemUtils;


class PostgresAggregateStoreTest {

    private DatabaseConfiguration databaseConfiguration;
    private DatabaseInitializer databaseInitializer;
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

        databaseConfiguration = new DatabaseConfiguration(
                SystemUtils.getPostgresUrl(),
                "event_sourcing_user",
                "event_sourcing_password");
        databaseInitializer = new DatabaseInitializer(databaseConfiguration);
        databaseInitializer.initialize();
    }

    @AfterEach
    void tearDown() {
        databaseInitializer.cleanup();
    }

    @Test
    void store() {
        // given
        Wallet wallet = new Wallet(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN);
        PostgresAggregateStore<Wallet> postgresAggregateStore = new PostgresAggregateStore<>(new JacksonJsonConverter(), databaseConfiguration);
        postgresAggregateStore.put(wallet);

        // when
        Optional<Wallet> maybeAggregate = postgresAggregateStore.get(wallet.getId(), Wallet.class);
        // then
        assertThat(maybeAggregate)
                .isPresent()
                .get()
                .isEqualTo(wallet);

    }

    @Test
    void getNotExisting() {
        // given
        Wallet wallet = new Wallet(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN);
        PostgresAggregateStore<Wallet> postgresAggregateStore = new PostgresAggregateStore<>(new JacksonJsonConverter(), databaseConfiguration);

        // when
        Optional<Wallet> maybeAggregate = postgresAggregateStore.get(wallet.getId(), Wallet.class);
        // then
        assertThat(maybeAggregate).isEmpty();

    }


    @Test
    void putExisting() {
        // given
        Wallet wallet = new Wallet(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN);
        PostgresAggregateStore<Wallet> postgresAggregateStore = new PostgresAggregateStore<>(new JacksonJsonConverter(), databaseConfiguration);
        postgresAggregateStore.put(wallet);

        Wallet sameWallet = new Wallet(wallet.id(), wallet.ownerId(), BigDecimal.ONE);
        // when
        postgresAggregateStore.put(sameWallet);

        // then
        Optional<Wallet> maybeAggregate = postgresAggregateStore.get(wallet.getId(), Wallet.class);
        assertThat(maybeAggregate)
                .isPresent()
                .get()
                .isEqualTo(sameWallet);


    }

}