package utils;

import java.util.Optional;

import antessio.eventsourcing.containers.PostgresContainer;

public final class SystemUtils {
    private SystemUtils(){

    }

    public static boolean isTestContainerEnabled() {
        return Optional.ofNullable(System.getenv("testContainerEnabled")).map(Boolean::valueOf).orElse(false);
    }
    public static String getPostgresUrl(){
        if (isTestContainerEnabled()){
            return PostgresContainer.getUrl();
        }else{
            return "jdbc:postgresql://localhost:5432/antessio_event_sourcing";
        }
    }


}
