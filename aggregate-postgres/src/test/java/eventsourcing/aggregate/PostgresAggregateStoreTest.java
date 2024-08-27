package eventsourcing.aggregate;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eventsourcing.aggregate.wallet.Wallet;
import jsonconversion.JacksonJsonConverter;


class PostgresAggregateStoreTest {

    private DatabaseConfiguration databaseConfiguration;
    private DatabaseInitializer databaseInitializer;


    @BeforeEach
    void setUp() {

        databaseConfiguration = new DatabaseConfiguration(
                "jdbc:postgresql://localhost:5432/antessio_event_sourcing",
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