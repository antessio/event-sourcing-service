package utils;

import java.util.Optional;

import antessio.eventsourcing.containers.PostgresContainer;

public final class SystemUtils {
    private SystemUtils(){

    }

    public static boolean isTestContainerEnabled() {
        String testContainerEnabled = System.getenv("testContainerEnabled");
        System.out.println("testContainerEnabled = " + testContainerEnabled);
        return Optional.ofNullable(testContainerEnabled).map(Boolean::valueOf).orElse(false);
    }
    public static String getPostgresUrl(){
        if (isTestContainerEnabled()){
            return PostgresContainer.getUrl();
        }else{
            return "jdbc:postgresql://localhost:5432/antessio_event_sourcing";
        }
    }
    public static String getPostgresUser(){
        if (isTestContainerEnabled()){
            return PostgresContainer.getUrl();
        }else{
            return "jdbc:postgresql://localhost:5432/antessio_event_sourcing";
        }
    }


}
