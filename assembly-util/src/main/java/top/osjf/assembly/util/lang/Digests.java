package top.osjf.assembly.util.lang;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;

/**
 * Digest tool class function implementation in  {@link DigestUtils}.
 *
 * @author zpf
 * @since 1.0.3
 */
public final class Digests extends DigestUtils {

    /*** @deprecated */
    private Digests() {
    }

    private Digests(MessageDigest digest) {
        super(digest);
    }

    private Digests(String name) {
        super(name);
    }
}
