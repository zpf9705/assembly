package top.osjf.assembly.simplified.service.annotation;

/**
 * Enumeration of types selected for service context configuration loading.
 *
 * @author zpf
 * @since 2.0.6
 */
public enum Type {

    /**
     * @see top.osjf.assembly.simplified.service.context.ClassesServiceContext
     */
    CLASSES,

    /**
     * @see top.osjf.assembly.simplified.service.context.SimpleServiceContext
     */
    @Deprecated
    SIMPLE
}
