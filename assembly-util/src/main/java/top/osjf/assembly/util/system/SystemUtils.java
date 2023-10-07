package top.osjf.assembly.util.system;

import top.osjf.assembly.util.annotation.CanNull;

import java.util.function.Function;

/**
 * Here is some tips on some API support system of Java classes.
 *
 * @author zpf
 * @since 1.0.0
 */
public abstract class SystemUtils {

    /**
     * The current working directory of the user
     */
    public static final String PROJECT_PATH = "user.dir";

    private static final String currentProjectPath;

    static {
        /*
         * Static access to relevant variables of the system
         * */
        currentProjectPath = System.getProperty(PROJECT_PATH);
    }

    /**
     * Set system key and value constant or configuration or cache value.
     *
     * @param key   must no be {@literal null}.
     * @param value must no be {@literal null}.
     * @param <K>   generic of {@code key}.
     * @param <V>   generic of {@code value}.
     */
    public static <K, V> void setProperty(K key, V value) {
        if (key == null || value == null) {
            return;
        }
        System.setProperty(key.toString(), value.toString());
    }

    /**
     * Get system key and value constant or configuration or cache value.
     *
     * @param key Value of {@code key}.
     * @return You call parameter value of key value.
     */
    public static String getProperty(String key) {
        if (key == null) {
            return null;
        }
        return System.getProperty(key);
    }

    /**
     * Get system key and value constant or configuration or cache value with convert.
     *
     * @param key     Value of {@code key}.
     * @param <C>     Convert generic.
     * @param convert Convert need type.
     * @param def     {@literal null} with default value.
     * @return You call parameter value of key value.
     */
    public static <C> C getPropertyWithConvert(String key, Function<String, C> convert, @CanNull C def) {
        if (key == null) {
            return def;
        }
        String property = getProperty(key);
        if (property == null) {
            return def;
        }
        return convert.apply(property);
    }

    /**
     * Get current project path.
     *
     * @return The startup path of the Java project.
     */
    public static String getCurrentProjectPath() {
        return currentProjectPath;
    }
}
