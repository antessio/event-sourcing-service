package eventsourcing.aggregate;

public class InitDatabase {

    public static void main(String[] args) {
        new AggregateStoreDatabaseInitializer(AggregateStoreDatabaseConfiguration.fromEnvironmentVariables())
                .initialize();
    }

}

