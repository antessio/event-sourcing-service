package antessio.utils;

import java.util.function.Consumer;

/**
 * Wraps a function that throws checked exception.
 * @param <T>
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {

    T get() throws Exception;
    static <T> T trapError(ThrowingSupplier<T> couldThrow) {
        try {
            return couldThrow.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static <T> T trapError(ThrowingSupplier<T> couldThrow, Consumer<Exception> exceptionConsumer) {
        try {
            return couldThrow.get();
        } catch (Exception e) {
            exceptionConsumer.accept(e);
            throw new RuntimeException(e);
        }
    }
}
