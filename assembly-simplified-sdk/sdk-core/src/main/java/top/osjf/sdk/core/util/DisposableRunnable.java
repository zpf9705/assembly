package top.osjf.sdk.core.util;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * The interface for releasing {@link Disposable} resources after integrating
 * {@link Runnable} subscription actions.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
interface DisposableRunnable extends Runnable, Disposable {
}
