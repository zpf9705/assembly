package top.osjf.assembly.util.lang;

import java.security.MessageDigest;

/**
 * Digest utils from {@link org.apache.commons.codec.digest.DigestUtils}, and this category
 * points out the expansion function.
 *
 * @author zpf
 * @since 1.0.4
 */
public class DigestUtils extends org.apache.commons.codec.digest.DigestUtils {

    public DigestUtils(MessageDigest digest) {
        super(digest);
    }

    public DigestUtils(String name) {
        super(name);
    }
}
