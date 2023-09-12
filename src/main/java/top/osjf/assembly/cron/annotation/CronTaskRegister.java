package top.osjf.assembly.cron.annotation;

import copy.cn.hutool.v_5819.core.util.ArrayUtil;
import copy.cn.hutool.v_5819.core.util.ReflectUtil;
import copy.cn.hutool.v_5819.cron.CronException;
import org.slf4j.Logger;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import top.osjf.assembly.cron.CronWithBeanCallRegister;
import top.osjf.assembly.cron.CronWithInstanceCallRegister;
import top.osjf.assembly.utils.ScanUtils;

/**
 * Timer task initiation processing registration class, which can rely on {@link ImportSelector} to
 * complete registration in the form of beans.
 * <p>
 * Trigger based on timed task switch annotation {@link EnableCronTaskRegister}, scan the package
 * path provided by it, find the corresponding method with {@link Cron} annotation, and determine
 * whether the calling object uses spring proxy or each instance invocation method through {@link Type} type
 *
 * @author zpf
 * @since 1.1.0
 */
public class CronTaskRegister implements DeferredImportSelector {

    @Override
    @NonNull
    public String[] selectImports(@NonNull AnnotationMetadata metadata) {
        //get Attributes for EnableCronTaskRegister
        AnnotationAttributes attributes =
                AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(EnableCronTaskRegister.class.getName()));
        if (attributes == null) {
            throw new CronException("Analysis named" + EnableCronTaskRegister.class.getName() + "annotation " +
                    "to AnnotationAttributes failed");
        }
        scanPackage = attributes.getStringArray("basePackages");
        if (ArrayUtil.isEmpty(scanPackage)) {
            scanPackage = ScanUtils.findSpringApplicationPackageName();
        }
        noMethodDefaultStart = attributes.getBoolean("noMethodDefaultStart");
        //instance logger
        Class<? extends Logger> loggerClazz = attributes.getClass("logger");
        logger = ReflectUtil.newInstance(loggerClazz);
        //find type
        Type type = attributes.getEnum("type");
        //Load different configuration classes based on the survival method of object calls
        if (type == Type.PROXY) {
            return new String[]{CronWithBeanCallRegister.class.getName()};
        } else if (type == Type.INSTANCE) {
            return new String[]{CronWithInstanceCallRegister.class.getName()};
        }
        return new String[0];
    }

    private static String[] scanPackage;

    private static boolean noMethodDefaultStart;

    private static Logger logger;

    public static String[] getScanPackage() {
        return scanPackage;
    }

    public static boolean isNoMethodDefaultStart() {
        return noMethodDefaultStart;
    }

    public static Logger getLogger() {
        if (logger == null) {
            logger = ReflectUtil.newInstance(EnableCronTaskRegister.CronSlf4j.class);
        }
        return logger;
    }
}
