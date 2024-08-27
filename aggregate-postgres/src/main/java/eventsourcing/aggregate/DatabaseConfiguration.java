package eventsourcing.aggregate;

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
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

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
