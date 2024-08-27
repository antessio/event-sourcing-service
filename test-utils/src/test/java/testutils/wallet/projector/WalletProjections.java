package testutils.wallet.projector;

import java.util.List;
import java.util.Optional;


import eventsourcing.Projection;
import eventsourcing.Projector;
import testutils.wallet.Wallet;
import testutils.wallet.events.WalletCreatedEvent;
import testutils.wallet.events.WalletTopUpExecuted;


public final class WalletProjections {

    private static final List<? extends Projector<Wallet, ? extends Record>> projectors = List.of(
            new WalletWalletCreatedEventProjector(),
            new WalletWalletTopUpExecutedProjector()
    );

    private WalletProjections() {
    }

    public static List<? extends Projector<Wallet, ? extends Record>> getProjectors() {
        return projectors;
    }


    @Projection(aggregateType = Wallet.class, eventType = WalletTopUpExecuted.class)
    public static class WalletWalletTopUpExecutedProjector implements Projector<Wallet, WalletTopUpExecuted> {
        public WalletWalletTopUpExecutedProjector(){}

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

    }

    @Projection(aggregateType = Wallet.class, eventType = WalletCreatedEvent.class)
    public static class WalletWalletCreatedEventProjector implements Projector<Wallet, WalletCreatedEvent> {
        public WalletWalletCreatedEventProjector(){}

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

    }

}
