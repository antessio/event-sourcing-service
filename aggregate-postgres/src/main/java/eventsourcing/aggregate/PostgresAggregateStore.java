package eventsourcing.aggregate;


import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.DSL;

import antessio.eventsourcing.jsonconversion.JsonConverter;

public class PostgresAggregateStore<A extends Aggregate> implements AggregateStore<A> {

    private static final Logger LOGGER = Logger.getLogger(PostgresAggregateStore.class.getCanonicalName());

    public static final Field<String> ID_FIELD = field("id", String.class);
    public static final Field<JSON> OBJECT_FIELD = field("object", JSON.class);
    public static final Field<String> TYPE_FIELD = field("type", String.class);
    public static final Table<Record> AGGREGATE_TABLE = table("event_sourcing.aggregate");
    private final JsonConverter jsonConverter;
    private final DatabaseConfiguration databaseConfiguration;

    public PostgresAggregateStore(
            JsonConverter jsonConverter,
            DatabaseConfiguration databaseConfiguration) {
        this.jsonConverter = jsonConverter;
        this.databaseConfiguration = databaseConfiguration;
    }


    @Override
    public Optional<A> get(String id, Class<? extends A> cls) {
        try (
                Connection conn = DriverManager.getConnection(
                        databaseConfiguration.getUrl(),
                        databaseConfiguration.getUser(),
                        databaseConfiguration.getPassword())
        ) {
            DSLContext create = DSL.using(conn, SQLDialect.POSTGRES);

            return Optional.ofNullable(create.select(ID_FIELD, OBJECT_FIELD, TYPE_FIELD)
                                             .from(AGGREGATE_TABLE)
                                             .where(ID_FIELD.eq(id)
                                                            .and(TYPE_FIELD.eq(cls.getCanonicalName())))
                                             .fetchAny())
                           .map(record -> record.get(OBJECT_FIELD))
                           .map(JSON::data)
                           .map(jsonData -> jsonConverter.fromJson(jsonData, cls));

        } catch (Exception e) {
            throw new eventsourcing.aggregate.DataAccessException(e);

        }
    }

    @Override
    public void put(A aggregate) {
        try (
                Connection conn = DriverManager.getConnection(
                        databaseConfiguration.getUrl(),
                        databaseConfiguration.getUser(),
                        databaseConfiguration.getPassword())
        ) {
            DSLContext create = DSL.using(conn, SQLDialect.POSTGRES);
            JSON objectJson = JSON.valueOf(jsonConverter.toJson(aggregate));
            create
                    .insertInto(AGGREGATE_TABLE, ID_FIELD, OBJECT_FIELD, TYPE_FIELD)
                    .values(aggregate.getId(), objectJson, aggregate.getClass().getCanonicalName())
                    .onConflict(ID_FIELD, TYPE_FIELD)
                    .doUpdate()
                    .set(OBJECT_FIELD, objectJson)
                    .execute();
        } catch (Exception e) {
            throw new eventsourcing.aggregate.DataAccessException(e);
        }

    }

}
