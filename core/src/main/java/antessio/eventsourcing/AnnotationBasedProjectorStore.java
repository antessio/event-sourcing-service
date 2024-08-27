package antessio.eventsourcing;

import static antessio.utils.ThrowingSupplier.trapError;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import eventsourcing.Event;
import eventsourcing.Projection;
import eventsourcing.Projector;
import eventsourcing.ProjectorStore;
import eventsourcing.aggregate.Aggregate;

/**
 * Projection store implementation based on {@link Projection} annotation.
 * It scans the packages provided in its constructor, instantiate the {@link Projector}s and keeps them in memory.
 * @param <A>
 */
public class AnnotationBasedProjectorStore<A extends Aggregate> implements ProjectorStore<A> {

    private static final Logger LOGGER = Logger.getLogger(AnnotationBasedProjectorStore.class.getCanonicalName());
    private final Map<Class<? extends Event<A>>, Projector<A, Event<A>>> projectorsMap;

    /**
     *
     * @param projectorPackageList Packages to scan.
     */
    public AnnotationBasedProjectorStore(List<String> projectorPackageList) {
        projectorsMap = new HashMap<>();
        projectorPackageList
                .stream()
                .flatMap(p -> findProjectionClasses(p).stream())
                .map(this::getProjectorConstructor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(projectorConstructor -> trapError(projectorConstructor::newInstance))
                .forEach(this::addProjector);
    }


    @Override
    public boolean hasProjector(Class<? extends Event<A>> eventType) {
        return projectorsMap.containsKey(eventType);
    }

    @Override
    public void addProjector(Projector<A, Event<A>> projector) {
        if (projectorsMap.containsKey(projector.getSubscribedEvent())) {
            throw new IllegalArgumentException("this event has already a projector");
        }
        projectorsMap.put(projector.getSubscribedEvent(), projector);
    }

    @Override
    public Projector<A, Event<A>> getMatchingProjector(Class<? extends Event<A>> eventType) {
        return projectorsMap.get(eventType);
    }

    private List<Class<?>> findProjectionClasses(String packageName) {
        List<Class<?>> annotatedClasses = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = trapError(() -> classLoader.getResources(path));

        List<File> directories = new ArrayList<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            directories.add(new File(resource.getFile()));
        }

        for (File directory : directories) {
            annotatedClasses.addAll(findProjectorClasses(directory, packageName));
        }

        return annotatedClasses;
    }

    private List<Class<?>> findProjectorClasses(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    classes.addAll(findProjectorClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz = trapError(() -> Class.forName(className));

                    if (clazz.isAnnotationPresent(Projection.class)) {
                        classes.add(clazz);
                    }
                }
            }
        }
        return classes;
    }

    private Optional<Constructor<Projector<A, Event<A>>>> getProjectorConstructor(Class<?> clazz) {

        if (Projector.class.isAssignableFrom(clazz)) {
            Class<Projector<A, Event<A>>> projectorClass = (Class<Projector<A, Event<A>>>) clazz;
            Constructor<Projector<A, Event<A>>> projectorConstructor =
                    null;
            try {
                projectorConstructor = projectorClass.getConstructor();
                return Optional.of(projectorConstructor);
            } catch (NoSuchMethodException e) {
                LOGGER.log(Level.WARNING, "class %s is annotated as projector but has no empty constructor".formatted(clazz));
            }
        } else {
            LOGGER.log(Level.WARNING, "class %s is annotated as projector but doesn't implement projector interface".formatted(clazz));
        }
        return Optional.empty();
    }

}
