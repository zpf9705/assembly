package top.osjf.assembly.simplified.cron.annotation;

import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import top.osjf.assembly.simplified.support.MappedAnnotationAttributes;
import top.osjf.assembly.util.lang.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Due to the use of {@link org.springframework.core.annotation.AliasFor} annotations
 * in {@link Cron}, but not using Spring's parsing during the registration process,
 * it is necessary to manually call {@link AnnotationUtils#getAnnotationAttributes(Annotation)}
 * for directed parsing in this class and use {@link AnnotationAttributes}'s API to simplify
 * the retrieval process.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
@SuppressWarnings("serial")
public final class CronAnnotationAttributes extends MappedAnnotationAttributes {

    /**
     * Create a {@link MappedAnnotationAttributes} encapsulated map structure using {@link Map map}.
     *
     * @param map original source of annotation attribute <em>key-value</em> pairs
     */
    public CronAnnotationAttributes(Map<String, Object> map) {
        super(map);
    }

    /**
     * Static for Create a {@link CronAnnotationAttributes} using {@link Method cronMethod}.
     *
     * @param cronMethod {@link Cron} proxy Method.
     * @return {@link Cron} Attributes.
     */
    public static CronAnnotationAttributes of(Method cronMethod) {
        return new CronAnnotationAttributes(of(cronMethod, Cron.class));
    }

    /**
     * Returning {@link Cron#expression()}, because {@link org.springframework.core.annotation.AliasFor}
     * was used but {@link AnnotationUtils#getAnnotationAttributes(Annotation)} was
     * adopted in the static method, which is compatible with support for
     * {@link org.springframework.core.annotation.AliasFor}, even if {@link Cron#value()}
     * is assigned, {@link Cron#expression()} will still redirect to obtain the same value.
     * <p>The default value is {@link Cron#DEFAULT_CRON_EXPRESSION}.
     *
     * @return {@link Cron#expression()}.
     */
    public String getExpression() {
        String cronExpression = getString(Cron.SELECT_OF_EXPRESSION_NAME);
        if (StringUtils.isBlank(cronExpression)) cronExpression = Cron.DEFAULT_CRON_EXPRESSION;
        return cronExpression;
    }

    /**
     * @return {@link Cron#profiles()}.
     */
    public String[] getProfiles() {
        return getStringArray(Cron.SELECT_OF_PROFILES_NAME);
    }
}
