package top.osjf.sdk.core.client;

import top.osjf.sdk.core.process.Response;

import java.io.Closeable;
import java.io.Serializable;

/**
 * A client help interface created for the request process of SDK.
 *
 * <p>The execution input sequence is:
 * <dl>
 *     <dt>1、{@link RequestCore}Request Remote</dt>
 *     <dt>2、{@link PreProcessingResponseHandler}Request data preprocessing</dt>
 *     <dt>3、{@link ResponseConvert}Convert Unified Request Object</dt>
 *     <dt>4、{@link Closeable}Clear memory parameters</dt>
 *     <dt>5、{@link LoggerConsumer}Define output log entries</dt>
 * </dl>
 * Each process has a corresponding interface method, which you can override to customize method conversion.
 *
 * <p>There are also well-defined abstract classes {@link AbstractClient} here to learn about.</p>
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface Client<R extends Response> extends RequestCore<R>, PreProcessingResponseHandler<R>,
        ResponseConvert<R>, LoggerConsumer, Closeable, Serializable {
}
