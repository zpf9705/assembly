package top.osjf.assembly.cache.autoconfigure;

/**
 * Launch the console logo input interface class to output the dynamic session component logo.
 *
 * @author zpf
 * @since 1.0.0
 */
public interface StartUpBanner {

    /**
     * Obtain the string layout of the logo text.
     *
     * @return Suggest not to use {@literal null}.
     */
    String getBanner();

    /**
     * Obtain the component name identifier as the unique name of the modified component.
     *
     * @return Suggest not to use {@literal null}.
     */
    String getLeftSign();
}
