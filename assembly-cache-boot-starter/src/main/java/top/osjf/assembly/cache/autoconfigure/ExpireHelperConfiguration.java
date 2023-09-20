package top.osjf.assembly.cache.autoconfigure;

/**
 * On the Public Configuration Selection Class of {@code ExpireHelper}
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireHelperConfiguration {

    public final ExpireProperties properties;

    public ExpireHelperConfiguration(ExpireProperties properties) {
        this.properties = properties;
    }

    public final ExpireProperties getProperties() {
        return this.properties;
    }
}
