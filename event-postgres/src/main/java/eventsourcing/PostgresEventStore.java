package eventsourcing;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Query;
import org.jooq.Record6;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.DSL;

import antessio.eventsourcing.jsonconversion.JsonConverter;
import eventsourcing.aggregate.Aggregate;


public class PostgresEventStore<A extends Aggregate> implements EventStore {

    private final static Table<?> EVENT_TABLE = table("event_sourcing.event");
    private final static Field<String> ID_FIELD = field("id", String.class);
    private final static Field<String> AGGREGATE_ID_FIELD = field("aggregate_id", String.class);
    private static final Field<JSON> OBJECT_FIELD = field("object", JSON.class);
    private final static Field<String> TYPE_FIELD = field("type", String.class);
    private final static Field<String> AGGREGATE_TYPE_FIELD = field("aggregate_type", String.class);
    private final static Field<Boolean> PROCESSED_FIELD = field("processed", Boolean.class);
    private final static Field<Instant> OCCURRED_AT_FIELD = field("occurred_at", Instant.class);
    private final JsonConverter jsonConverter;
    private final EventStoreDatabaseConfiguration eventStoreDatabaseConfiguration;

    public PostgresEventStore(
            JsonConverter jsonConverter,
            EventStoreDatabaseConfiguration eventStoreDatabaseConfiguration) {
        this.jsonConverter = jsonConverter;
        this.eventStoreDatabaseConfiguration = eventStoreDatabaseConfiguration;
    }


    @Override
    public <A extends Aggregate> List<Event<A>> getAllEvents() {

        return execute(dslContext -> {
            return dslContext
                    .select(ID_FIELD, AGGREGATE_ID_FIELD, OBJECT_FIELD, TYPE_FIELD, AGGREGATE_TYPE_FIELD, PROCESSED_FIELD)
                    .from(EVENT_TABLE)
                    .fetch()
                    .stream()
                    .map(r -> {

                        String json = r.get(OBJECT_FIELD).data();
                        Class<Event<A>> eventClass = getEventClass(r);
                        return jsonConverter.fromJson(json, eventClass);
                    })
                    .toList();
        });
    }


    @Override
    public<A extends Aggregate> List<Event<A>> getUnprocessedEvents() {
        return execute(dslContext -> {
            return dslContext
                    .select(ID_FIELD, AGGREGATE_ID_FIELD, OBJECT_FIELD, TYPE_FIELD, AGGREGATE_TYPE_FIELD, PROCESSED_FIELD)
                    .from(EVENT_TABLE)
                    .where(PROCESSED_FIELD.eq(false))
                    .orderBy(OCCURRED_AT_FIELD.asc())
                    .fetch()
                    .stream()
                    .map(r -> {

                        String json = r.get(OBJECT_FIELD).data();
                        Class<Event<A>> eventClass = getEventClass(r);
                        return jsonConverter.fromJson(json, eventClass);
                    })
                    .toList();
        });
    }


    @Override
    public <A extends Aggregate> void put(List<Event<A>> events) {
        execute(dslContext -> {
            dslContext.batch(events
                                     .stream()
                                     .map(e -> convertToInsert(e, dslContext, false))
                                     .toList())
                      .execute();
        });
    }

    @Override
    public <A extends Aggregate> List<Event<A>> getAggregateEvents(Class<? extends A> aggregateClass) {
        return execute(dslContext -> {
            return dslContext
                    .select(ID_FIELD, AGGREGATE_ID_FIELD, OBJECT_FIELD, TYPE_FIELD, AGGREGATE_TYPE_FIELD, PROCESSED_FIELD)
                    .from(EVENT_TABLE)
                    .where(AGGREGATE_TYPE_FIELD.eq(aggregateClass.getCanonicalName()))
                    .fetch()
                    .stream()
                    //.map(r -> convertRecordToEvent(r, getEventClass(r), getAggregateClass(r)))
                    .map(r -> {

                        String json = r.get(OBJECT_FIELD).data();
                        Class<Event<A>> eventClass = getEventClass(r);
                        return jsonConverter.fromJson(json, eventClass);
                    })
                    .toList();
        });
    }

    @Override
    public <A extends Aggregate> void markAsProcessed(List<Event<A>> processedEvents) {
        execute(dlsContext -> {
            dlsContext
                    .update(EVENT_TABLE)
                    .set(PROCESSED_FIELD, true)
                    .where(ID_FIELD.in(processedEvents.stream().map(Event::getEventId).map(UUID::toString).toList()))
                    .execute();
        });
    }

    private <A extends Aggregate> Class<Event<A>> getEventClass(Record6<String, String, JSON, String, String, Boolean> r) {
        try {
            return (Class<Event<A>>) Class.forName(r.get(TYPE_FIELD));

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T execute(Function<DSLContext, T> dslContextTFunction) {
        try (
                Connection conn = DriverManager.getConnection(
                        eventStoreDatabaseConfiguration.getUrl(),
                        eventStoreDatabaseConfiguration.getUser(),
                        eventStoreDatabaseConfiguration.getPassword())
        ) {
            DSLContext create = DSL.using(conn, SQLDialect.POSTGRES);

            return dslContextTFunction.apply(create);

        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    private void execute(Consumer<DSLContext> dslContextConsumer) {
        try (
                Connection conn = DriverManager.getConnection(
                        eventStoreDatabaseConfiguration.getUrl(),
                        eventStoreDatabaseConfiguration.getUser(),
                        eventStoreDatabaseConfiguration.getPassword())
        ) {
            DSLContext create = DSL.using(conn, SQLDialect.POSTGRES);

            dslContextConsumer.accept(create);

        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    private <A extends Aggregate> Query convertToInsert(Event<A> event, DSLContext create, boolean isProcessed) {
        return create.insertInto(
                             EVENT_TABLE,
                             ID_FIELD,
                             AGGREGATE_ID_FIELD,
                             OBJECT_FIELD,
                             TYPE_FIELD,
                             AGGREGATE_TYPE_FIELD,
                             PROCESSED_FIELD,
                             OCCURRED_AT_FIELD)
                     .values(
                             event.getEventId().toString(),
                             event.getAggregateId(),
                             JSON.valueOf(jsonConverter.toJson(event)),
                             event.getClass().getCanonicalName(),
                             event.getAggregateClass().getCanonicalName(),
                             isProcessed,
                             event.getOccurredAt()
                     );

    }
}
