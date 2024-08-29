package antessio.eventsourcing;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import antessio.eventsourcing.inmemory.InMemoryAggregateStore;
import antessio.eventsourcing.inmemory.InMemoryEventStore;
import antessio.eventsourcing.inmemory.InMemoryProjectorStore;
import eventsourcing.Event;
import eventsourcing.EventStore;
import eventsourcing.ProjectorStore;
import eventsourcing.aggregate.AggregateStore;
import testutils.wallet.Wallet;
import testutils.wallet.events.WalletCreatedEvent;
import testutils.wallet.events.WalletTopUpExecuted;
import testutils.wallet.projector.WalletProjections;

public class BiTest {
    private ProjectorStore projectorStore;
    private AggregateStore aggregateStore;
    private EventStore eventStore;
    private ReadStoreService readStore;

    @BeforeEach
    void setUp() {
        projectorStore = new InMemoryProjectorStore();
        aggregateStore = new InMemoryAggregateStore();
        eventStore = new InMemoryEventStore();

        readStore = new ReadStoreService(projectorStore, aggregateStore, eventStore);
        WalletProjections.getProjectors().forEach(readStore::registerProjector);
    }

    @Test
    void shouldListenToTopicAndMaterialiseAggregate() {
        Instant now = Instant.now();
        UUID newWalletId = UUID.randomUUID();
        UUID newWalletOwnerId = UUID.randomUUID();
        Wallet wallet = new Wallet(UUID.randomUUID(), BigDecimal.TEN, UUID.randomUUID());
        List<Event<Wallet>> unprocessedEvents = List.of(
                new WalletCreatedEvent(UUID.randomUUID(), newWalletId, newWalletOwnerId, new BigDecimal(300), now.minus(10, ChronoUnit.MINUTES)),
                new WalletTopUpExecuted(UUID.randomUUID(), wallet.id(), new BigDecimal(10), now.minus(9, ChronoUnit.MINUTES)),
                new WalletTopUpExecuted(UUID.randomUUID(), newWalletId, new BigDecimal(310), now.minus(8, ChronoUnit.MINUTES)));
        List<Event<Wallet>> processedEvents = List.of(new WalletCreatedEvent(
                UUID.randomUUID(),
                wallet.id(),
                wallet.ownerId(),
                wallet.amount(),
                now.minus(15, ChronoUnit.MINUTES)));
        readStore.getAggregateStore().put(wallet);
        readStore.getEventStore().put(
                processedEvents
        );
        readStore.getEventStore().markAsProcessed(processedEvents);
        readStore.getEventStore().put(unprocessedEvents);

        readStore.processEvents();
        assertThat(readStore.getAggregate(wallet.getId(), Wallet.class))
                .get()
                .matches(w -> w.amount().intValue() == 20   , "amount should be 20");
        assertThat(readStore.getAggregate(newWalletId.toString(), Wallet.class))
                .get()
                .matches(w -> w.ownerId().equals(newWalletOwnerId))
                .matches(w -> w.amount().intValue() == 610);
    }

}
