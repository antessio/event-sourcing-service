package eventsourcing;

public final class DatabaseConfiguration {

    private final String url;
    private final String user;
    private final String password;


    public DatabaseConfiguration(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DatabaseConfiguration fromEnvironmentVariables() {
        String url = System.getenv("EVENT_STORE_DB_URL");
        String user = System.getenv("EVENT_STORE_DB_USER");
        String password = System.getenv("EVENT_STORE_DB_PASSWORD");

        return new DatabaseConfiguration(url, user, password);

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
