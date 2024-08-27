package eventsourcing.aggregate;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import eventsourcing.aggregate.wallet.Wallet;


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

        // when
        // then
        assertThatExceptionOfType(DataAccessException.class)
                .isThrownBy(() -> postgresAggregateStore.put(wallet));


    }

    static class JacksonJsonConverter implements JsonConverter {

        private final ObjectMapper objectMapper;

        public JacksonJsonConverter() {
            objectMapper = new ObjectMapper();
        }

        @Override
        public <T> T fromJson(String json, Class<? extends T> cls) {
            try {
                return objectMapper.readValue(json, cls);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public <T> String toJson(T obj) {
            try {
                return objectMapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

    }

}