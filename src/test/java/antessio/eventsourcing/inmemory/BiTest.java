package antessio.eventsourcing.inmemory;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import antessio.eventsourcing.AggregateStore;
import antessio.eventsourcing.Event;
import antessio.eventsourcing.EventStore;
import antessio.eventsourcing.ProjectorStore;
import antessio.eventsourcing.ReadStoreService;
import antessio.eventsourcing.inmemory.wallet.Wallet;
import antessio.eventsourcing.inmemory.wallet.events.WalletCreatedEvent;
import antessio.eventsourcing.inmemory.wallet.events.WalletTopUpExecuted;
import antessio.eventsourcing.inmemory.wallet.projector.WalletProjections;

public class BiTest {
    private ProjectorStore<Wallet, UUID> projectorStore;
    private AggregateStore<Wallet, UUID> aggregateStore;
    private EventStore<Wallet, UUID> eventStore;
    private ReadStoreService<Wallet, UUID> readStore;

    @BeforeEach
    void setUp() {
        projectorStore = new InMemoryProjectorStore();
        aggregateStore = new InMemoryAggregateStore();
        eventStore = new InMemoryEventStore();

        readStore = new ReadStoreService<>(projectorStore, aggregateStore, eventStore);
        WalletProjections.registerProjections(readStore);
    }

    @Test
    void shouldListenToTopicAndMaterialiseAggregate() {
        Instant now = Instant.now();
        UUID newWalletId = UUID.randomUUID();
        UUID newWalletOwnerId = UUID.randomUUID();
        Wallet wallet = new Wallet(UUID.randomUUID(), BigDecimal.TEN, UUID.randomUUID());
        List<Event<Wallet, UUID>> unprocessedEvents = List.of(
                new WalletCreatedEvent(UUID.randomUUID(), newWalletId, newWalletOwnerId, new BigDecimal(300), now.minus(10, ChronoUnit.MINUTES)),
                new WalletTopUpExecuted(UUID.randomUUID(), wallet.getId(), new BigDecimal(10),now.minus(9, ChronoUnit.MINUTES)),
                new WalletTopUpExecuted(UUID.randomUUID(), newWalletId, new BigDecimal(310), now.minus(8, ChronoUnit.MINUTES)));
        readStore.getAggregateStore().put(wallet);
        readStore.getEventStore().put(
                List.of(new WalletCreatedEvent(UUID.randomUUID(), wallet.getId(), wallet.ownerId(), wallet.amount(), now.minus(15, ChronoUnit.MINUTES)))
        );

        readStore.processEvents(unprocessedEvents);
        assertThat(readStore.getAggregate(wallet.getId()))
                .get()
                .matches(w -> w.amount().intValue() == 20   , "amount should be 310");
        assertThat(readStore.getAggregate(newWalletId))
                .get()
                .matches(w -> w.ownerId().equals(newWalletOwnerId))
                .matches(w -> w.amount().intValue() == 610);
    }

}
