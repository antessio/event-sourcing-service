package eventsourcing;

import static org.jooq.impl.DSL.table;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.DSL;


public class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getCanonicalName());
    private final static Table<?> EVENT_TABLE = table("event_sourcing.event");
    private final String url;
    private final String user;
    private final String password;

    public DatabaseInitializer(DatabaseConfiguration databaseConfiguration) {
        this.url =databaseConfiguration.getUrl();
        this.user = databaseConfiguration.getUser();
        this.password = databaseConfiguration.getPassword();
    }

    public void initialize() {
        try (
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement()
        ) {

            InputStream inputStream = InitDatabase.class.getResourceAsStream("/event_store_init.sql");
            String sql = new String(inputStream.readAllBytes());
            // Execute the SQL script
            stmt.executeUpdate(sql);
            LOGGER.log(Level.INFO, "Database initialized successfully.");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "unable to initialize the database", e);
        }
    }

    public void cleanup(){
        try (
                Connection conn = DriverManager.getConnection(url, user, password);
        ) {

            DSLContext create = DSL.using(conn, SQLDialect.POSTGRES);
            create.dropTable(EVENT_TABLE)
                  .execute();
            LOGGER.log(Level.INFO, "Database cleaned up successfully.");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "unable to cleanup the database", e);
        }
    }

}
