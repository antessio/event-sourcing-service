package eventsourcing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import eventsourcing.aggregate.Aggregate;

/**
 * Annotate a {@link Projector} class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Projection{

    Class<? extends Aggregate> aggregateType();
    Class<? extends Event<? extends Aggregate>> eventType();
}
