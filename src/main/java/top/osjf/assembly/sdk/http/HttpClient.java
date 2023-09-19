package top.osjf.assembly.sdk.http;


import java.lang.annotation.*;

/**
 * Identify the type of HTTP tool used.
 *
 * @author zpf
 * @since 1.1.1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpClient {

    Type type() default Type.APACHE_HTTP;
}
