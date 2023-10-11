package top.osjf.assembly.cache.autoconfigure;

import top.osjf.assembly.util.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * Used to retrieve and obtain resource versions, there is currently no specific combination
 * of dependent versions while maintaining the JDK version at 1.8.
 *
 * @author zpf
 * @since 1.0.0
 */
public final class CacheVersion {

    private static final Attributes.Name[] other_version = {
            new Attributes.Name("Bundle-Version"),
            new Attributes.Name("Manifest-Version"),
            new Attributes.Name("Archiver-Version")
    };

    private CacheVersion() {
    }

    public static String getVersion(Class<?> sourceClass) {
        if (sourceClass == null) return "UNKNOWN";
        return determineExpireVersion(sourceClass);
    }

    private static String determineExpireVersion(Class<?> sourceClass) {
        String implementationVersion = sourceClass.getPackage().getImplementationVersion();
        if (implementationVersion != null) {
            return implementationVersion;
        }
        CodeSource codeSource = sourceClass.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return null;
        }
        URL codeSourceLocation = codeSource.getLocation();
        try {
            URLConnection connection = codeSourceLocation.openConnection();
            if (connection instanceof JarURLConnection) {
                return getImplementationVersion(((JarURLConnection) connection).getJarFile());
            }
            try (JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()))) {
                return getImplementationVersion(jarFile);
            }
        } catch (Exception ex) {
            return null;
        }
    }

    private static String getImplementationVersion(JarFile jarFile) throws IOException {
        Attributes attributes = jarFile.getManifest().getMainAttributes();
        String version = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        if (StringUtils.isBlank(version)) {
            for (Attributes.Name name : other_version) {
                String value = attributes.getValue(name);
                if (StringUtils.isNotBlank(value)){
                    version = value;
                    break;
                }
            }
        }
        return version;
    }
}
