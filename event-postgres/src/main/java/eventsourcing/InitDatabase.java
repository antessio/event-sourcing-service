package eventsourcing;

public class InitDatabase {

    public static void main(String[] args) {
        new EventStoreDatabaseInitializer(EventStoreDatabaseConfiguration.fromEnvironmentVariables())
                .initialize();
    }

}

