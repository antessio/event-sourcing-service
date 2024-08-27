package utils;

import java.util.Optional;

public final class SystemUtils {
    private SystemUtils(){

    }

    public static boolean isTestContainerEnabled() {
        return Optional.ofNullable(System.getenv("testContainerEnabled")).map(Boolean::valueOf).orElse(false);
    }

}
