package antessio.eventsourcing.containers;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainer {

    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>( "postgres:16-alpine")
            .withUsername("event_sourcing_user")
            .withPassword("event_sourcing_password")
            .withDatabaseName("antessio_event_sourcing");

    public static void start(){
        postgreSQLContainer.start();
    }
    public static void stop(){
        postgreSQLContainer.stop();
    }

    public static String getUrl(){
        checkIsRunning();
        return postgreSQLContainer.getJdbcUrl();
    }
    public static String getUsername(){
        checkIsRunning();
        return postgreSQLContainer.getUsername();
    }
    public static String getPassword(){
        checkIsRunning();
        return postgreSQLContainer.getPassword();
    }

    private static void checkIsRunning() {
        if (!postgreSQLContainer.isRunning()){
            throw new IllegalStateException("database is not running");
        }
    }

}
