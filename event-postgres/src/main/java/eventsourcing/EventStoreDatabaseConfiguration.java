package eventsourcing;

public final class EventStoreDatabaseConfiguration {

    private final String url;
    private final String user;
    private final String password;


    public EventStoreDatabaseConfiguration(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static EventStoreDatabaseConfiguration fromEnvironmentVariables() {
        String url = System.getenv("EVENT_STORE_DB_URL");
        String user = System.getenv("EVENT_STORE_DB_USER");
        String password = System.getenv("EVENT_STORE_DB_PASSWORD");

        return new EventStoreDatabaseConfiguration(url, user, password);

    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

}
