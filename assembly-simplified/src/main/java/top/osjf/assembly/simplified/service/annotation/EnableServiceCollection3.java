package top.osjf.assembly.simplified.service.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * The third version of service collection is more accurate
 * than the previous two versions, which collects rule bean
 * names during bean renaming.
 *
 * @see EnableServiceCollection
 * @see EnableServiceCollection2
 * @see ServiceContextRecordConfiguration
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ServiceContextRecordConfiguration.class,
        ServiceContextRecordConfiguration.ServiceContextRecordImportConfiguration.class})
public @interface EnableServiceCollection3 {
}
