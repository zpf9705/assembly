package top.osjf.assembly.util;

import cn.hutool.core.io.IoUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Closeable;

/**
 * For more methods on closing tools for {@link AutoCloseable}, you can follow {@link IoUtil}.
 *
 * @author zpf
 * @since 3.0.0
 */
public final class CloseableUtils extends IoUtil {

    /**
     * Close any resource with {@link Closeable}.
     *
     * @param closeables {@link Closeable}
     */
    public static void close(Closeable... closeables) {
        if (ArrayUtils.isEmpty(closeables)) {
            return;
        }
        for (Closeable closeable : closeables) {
            close(closeable);
        }
    }

    /**
     * Close any resource with {@link AutoCloseable}.
     *
     * @param closeables {@link AutoCloseable}
     */
    public static void close(AutoCloseable... closeables) {
        if (ArrayUtils.isEmpty(closeables)) {
            return;
        }
        for (AutoCloseable closeable : closeables) {
            close(closeable);
        }
    }
}
