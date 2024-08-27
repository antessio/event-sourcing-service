package eventsourcing.aggregate;

public class InitDatabase {

    public static void main(String[] args) {
        new DatabaseInitializer(DatabaseConfiguration.fromEnvironmentVariables())
                .initialize();
    }

}

