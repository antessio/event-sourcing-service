package antessio.eventsourcing.inmemory.wallet.projector;

import java.util.Optional;
import java.util.UUID;

import antessio.eventsourcing.EventSourcingService;
import antessio.eventsourcing.Projector;
import antessio.eventsourcing.inmemory.wallet.Wallet;
import antessio.eventsourcing.inmemory.wallet.events.WalletCreatedEvent;
import antessio.eventsourcing.inmemory.wallet.events.WalletTopUpExecuted;


public final class WalletProjections {

    private WalletProjections() {
    }

    public static void registerProjections(EventSourcingService<Wallet, UUID> EventSourcingService) {
        EventSourcingService.registerProjector(
                walletCreatedSubscription());
        EventSourcingService.registerProjector(
                walletTopUpExecutedSubscription());
    }

    private static Projector<Wallet, WalletTopUpExecuted> walletTopUpExecutedSubscription() {
        return new Projector<>() {
            @Override
            public Wallet handle(Wallet existingAggregate, WalletTopUpExecuted eventPayload) {
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


}
