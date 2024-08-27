package utils;

import java.util.Optional;

public final class SystemUtils {
    private SystemUtils(){

    }

    public static boolean isTestContainerEnabled() {
        String testContainerEnabled = System.getenv("testContainerEnabled");
        System.out.println("testContainerEnabled = " + testContainerEnabled);
        return Optional.ofNullable(testContainerEnabled).map(Boolean::valueOf).orElse(false);
    }

}
