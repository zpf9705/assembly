package top.osjf.assembly.util.io;

import cn.hutool.core.io.IoUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Closeable;

/**
 * IO related tools, thanks to {@link IoUtil}.
 *
 * @author zpf
 * @since 1.0.0
 */
public final class IoUtils extends IoUtil {
    private IoUtils() {
    }

    /**
     * Close any resource with {@link Closeable}.
     *
     * @param closeables {@link Closeable}
     */
    public static void closeAny(Closeable... closeables) {
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
    public static void closeAny(AutoCloseable... closeables) {
        if (ArrayUtils.isEmpty(closeables)) {
            return;
        }
        for (AutoCloseable closeable : closeables) {
            close(closeable);
        }
    }
}
