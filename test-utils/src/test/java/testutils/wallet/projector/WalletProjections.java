package testutils.wallet.projector;

import java.util.List;
import java.util.Optional;


import eventsourcing.Projector;
import testutils.wallet.Wallet;
import testutils.wallet.events.WalletCreatedEvent;
import testutils.wallet.events.WalletTopUpExecuted;


public final class WalletProjections {

    private static final List<? extends Projector<Wallet, ? extends Record>> projectors = List.of(
            walletCreatedSubscription(),
            walletTopUpExecutedSubscription()
    );

    private WalletProjections() {
    }

    public static List<? extends Projector<Wallet, ? extends Record>> getProjectors() {
        return projectors;
    }

    private static Projector<Wallet, WalletTopUpExecuted> walletTopUpExecutedSubscription() {
        return new Projector<>() {
            @Override
            public Wallet handle(Wallet existingAggregate, WalletTopUpExecuted eventPayload) {
                if (existingAggregate == null) {
                    throw new IllegalStateException("can't apply event to non-existing aggregate");
                }
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
