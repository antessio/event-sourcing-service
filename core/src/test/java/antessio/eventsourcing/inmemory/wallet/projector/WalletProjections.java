package antessio.eventsourcing.inmemory.wallet.projector;

import java.util.List;
import java.util.Optional;

import antessio.eventsourcing.EventSourcingService;
import antessio.eventsourcing.ReadStoreService;
import antessio.eventsourcing.inmemory.wallet.Wallet;
import antessio.eventsourcing.inmemory.wallet.events.WalletCreatedEvent;
import antessio.eventsourcing.inmemory.wallet.events.WalletTopUpExecuted;
import eventsourcing.Projector;


public final class WalletProjections {
    private static final List<? extends Projector<Wallet, ? extends Record>> projectors = List.of(
            walletCreatedSubscription(),
            walletTopUpExecutedSubscription()
    );

    private WalletProjections() {
    }

    public static void registerProjections(EventSourcingService<Wallet> eventSourcingService) {
        projectors.forEach(eventSourcingService::registerProjector);
    }

    private static Projector<Wallet, WalletTopUpExecuted> walletTopUpExecutedSubscription() {
        return new Projector<>() {
            @Override
            public Wallet handle(Wallet existingAggregate, WalletTopUpExecuted eventPayload) {
                if (existingAggregate == null)
                    throw new IllegalStateException("can't apply event to non-existing aggregate");
                return new Wallet(
                        existingAggregate.id(),
                        existingAggregate.amount()
                                         .add(eventPayload.amount()),
                        existingAggregate.ownerId());
            }

            @Override
            public Class<? extends WalletTopUpExecuted> getSubscribedEvent() {
                return WalletTopUpExecuted.class;
            }
        };
    }

    private static Projector<Wallet, WalletCreatedEvent> walletCreatedSubscription() {
        return new Projector<>() {
            @Override
            public Wallet handle(Wallet existingAggregate, WalletCreatedEvent eventPayload) {
                return Optional.ofNullable(existingAggregate)
                               .orElseGet(() -> new Wallet(
                                       eventPayload.id(),
                                       eventPayload.amount(),
                                       eventPayload.ownerId()));
            }

            @Override
            public Class<? extends WalletCreatedEvent> getSubscribedEvent() {
                return WalletCreatedEvent.class;
            }
        };
    }


    public static void registerProjections(ReadStoreService<Wallet> readStore) {
        projectors.forEach(readStore::registerProjector);
    }

}
