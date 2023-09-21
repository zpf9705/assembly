package top.osjf.assembly.cache.autoconfigure;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;
import top.osjf.assembly.util.annotation.CanNull;

import java.io.PrintStream;

/**
 * Component logo input tool，based on Spring's logo output method.
 *
 * @author zpf
 * @since 1.0.0
 */
public final class StartUpBannerExecutor {

    private StartUpBannerExecutor() {
    }

    public static void printBanner(@CanNull Environment environment,
                                   StartUpBanner banner, Class<?> sourceClass,
                                   PrintStream printStream) {
        printStream.println(banner.getBanner());
        String version = CacheVersion.getVersion(sourceClass);
        version = (version != null) ? " (v" + version + ")" : "";
        StringBuilder padding = new StringBuilder();
        while (padding.length() < 42 - (version.length() + banner.getLeftSign().length())) {
            padding.append(" ");
        }
        printStream.println(AnsiOutput.toString(AnsiColor.GREEN, banner.getLeftSign(), AnsiColor.DEFAULT,
                padding.toString(), AnsiStyle.FAINT, version));
        printStream.println();
    }
}
