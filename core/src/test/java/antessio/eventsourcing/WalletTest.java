package antessio.eventsourcing;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import antessio.eventsourcing.containers.PostgresContainer;
import antessio.eventsourcing.inmemory.InMemoryAggregateStore;
import antessio.eventsourcing.inmemory.InMemoryEventStore;
import antessio.eventsourcing.inmemory.InMemoryProjectorStore;
import eventsourcing.EventStoreDatabaseConfiguration;
import eventsourcing.EventStoreDatabaseInitializer;
import eventsourcing.PostgresEventStore;
import eventsourcing.aggregate.AggregateStoreDatabaseConfiguration;
import eventsourcing.aggregate.AggregateStoreDatabaseInitializer;
import eventsourcing.aggregate.PostgresAggregateStore;
import jsonconversion.JacksonJsonConverter;
import testutils.wallet.Wallet;
import testutils.wallet.commands.CreateWalletCommand;
import testutils.wallet.commands.TopUpWalletCommand;
import testutils.wallet.projector.WalletProjections;
import utils.SystemUtils;

class WalletTest {

    private AggregateStoreDatabaseConfiguration aggregateStoreDatabaseConfiguration;
    private AggregateStoreDatabaseInitializer aggregateStoreDatabaseInitializer;
    private EventStoreDatabaseConfiguration eventStoreDatabaseConfiguration;
    private EventStoreDatabaseInitializer eventStoreDatabaseInitializer;
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
        aggregateStoreDatabaseConfiguration = getAggregateStoreDatabaseConfiguration();
        aggregateStoreDatabaseInitializer = new AggregateStoreDatabaseInitializer(aggregateStoreDatabaseConfiguration);
        aggregateStoreDatabaseInitializer.initialize();
        eventStoreDatabaseConfiguration = getEventStoreDatabaseConfiguration();
        eventStoreDatabaseInitializer = new EventStoreDatabaseInitializer(eventStoreDatabaseConfiguration);
        eventStoreDatabaseInitializer.initialize();

    }


    @AfterEach
    void tearDown() {
        aggregateStoreDatabaseInitializer.cleanup();
        eventStoreDatabaseInitializer.cleanup();
    }


    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("provideImplementations")
    void shouldCreateWallet(String description, EventSourcingService eventStore) {
        UUID ownerId = UUID.randomUUID();

        Wallet walletCreated = eventStore.publish(new CreateWalletCommand(ownerId));
        assertThat(walletCreated)
                .isNotNull()
                .matches(w -> w.getId() != null)
                .matches(w -> w.ownerId().equals(ownerId))
                .matches(w -> w.amount().equals(BigDecimal.ZERO));

    }


    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("provideImplementations")
    void shouldTopUpWallet(String description, EventSourcingService eventStore) {
        Wallet wallet = new Wallet(UUID.randomUUID(), BigDecimal.TEN, UUID.randomUUID());
        eventStore.getAggregateStore().put(wallet);

        Wallet updatedWallet = eventStore.publish(new TopUpWalletCommand(wallet.id(), BigDecimal.valueOf(3000)));
        assertThat(updatedWallet)
                .isNotNull()
                .matches(w -> w.amount().intValue() == 3010);

        ;
    }

    public static Stream<Arguments> provideImplementations() {
        return Stream.of(
                Arguments.of("all in memory", allInMemory()),
                Arguments.of("only aggregate store on pg", allInMemoryWithAggregateStoreOnPg()),
                Arguments.of("only annotation based projector store", allInMemoryWithAnnotationBasedProjectorStore()),
                Arguments.of("only event store on pg", allInMemoryWithEventStoreOnPg()),
                Arguments.of("all on pg and annotation based projector store", annotationBasedProjectorAndAllOnPg())
        );
    }

    private static EventSourcingService allInMemory() {


        EventSourcingService eventStore = new EventSourcingService(
                new InMemoryProjectorStore(),
                new InMemoryAggregateStore(),
                new InMemoryEventStore());
        WalletProjections.getProjectors().forEach(eventStore::registerProjector);
        return eventStore;
    }

    private static EventSourcingService allInMemoryWithAggregateStoreOnPg() {


        EventSourcingService eventStore = new EventSourcingService(
                new InMemoryProjectorStore(),
                new PostgresAggregateStore(new JacksonJsonConverter(), getAggregateStoreDatabaseConfiguration()),
                new InMemoryEventStore());
        WalletProjections.getProjectors().forEach(eventStore::registerProjector);
        return eventStore;
    }

    private static EventSourcingService allInMemoryWithEventStoreOnPg() {


        EventSourcingService eventStore = new EventSourcingService(
                new InMemoryProjectorStore(),
                new InMemoryAggregateStore(),
                new PostgresEventStore<>(new JacksonJsonConverter(), getEventStoreDatabaseConfiguration()));
        WalletProjections.getProjectors().forEach(eventStore::registerProjector);
        return eventStore;
    }




    private static EventSourcingService allInMemoryWithAnnotationBasedProjectorStore() {

        return new EventSourcingService(
                new AnnotationBasedProjectorStore(List.of("testutils.wallet.projector")),
                new InMemoryAggregateStore(),
                new InMemoryEventStore());
    }

    private static EventSourcingService annotationBasedProjectorAndAllOnPg() {

        return new EventSourcingService(
                new AnnotationBasedProjectorStore(List.of("testutils.wallet.projector")),
                new PostgresAggregateStore(new JacksonJsonConverter(), getAggregateStoreDatabaseConfiguration()),
                new PostgresEventStore<>(new JacksonJsonConverter(), getEventStoreDatabaseConfiguration()));
    }

    private static @NotNull AggregateStoreDatabaseConfiguration getAggregateStoreDatabaseConfiguration() {
        return new AggregateStoreDatabaseConfiguration(
                SystemUtils.getPostgresUrl(),
                "event_sourcing_user",
                "event_sourcing_password");
    }

    private static @NotNull EventStoreDatabaseConfiguration getEventStoreDatabaseConfiguration() {
        return new EventStoreDatabaseConfiguration(
                SystemUtils.getPostgresUrl(),
                "event_sourcing_user",
                "event_sourcing_password");
    }

}