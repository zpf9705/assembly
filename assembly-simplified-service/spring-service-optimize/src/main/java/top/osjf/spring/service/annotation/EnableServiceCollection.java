package top.osjf.spring.service.annotation;

import org.springframework.context.annotation.Import;
import top.osjf.spring.service.ServiceContextUtils;

import java.lang.annotation.*;

/**
 * The third version of service collection is more accurate
 * than the previous two versions, which collects rule bean
 * names during bean renaming.
 *
 * <p>The collection of services has been changed to store within
 * the scope {@link ServiceContextUtils#SERVICE_SCOPE}.
 *
 * @see ServiceContextRecordConfiguration
 * @see ServiceContextRecordConfiguration.ServiceContextRecordImportConfiguration
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ServiceContextRecordConfiguration.class,
        ServiceContextRecordConfiguration.ServiceContextRecordImportConfiguration.class})
public @interface EnableServiceCollection {
}
