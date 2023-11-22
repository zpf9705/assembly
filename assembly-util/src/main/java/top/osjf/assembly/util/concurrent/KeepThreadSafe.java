package top.osjf.assembly.util.concurrent;

import java.lang.annotation.*;

/**
 * Keep thread safe sign.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.7
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KeepThreadSafe {
}
