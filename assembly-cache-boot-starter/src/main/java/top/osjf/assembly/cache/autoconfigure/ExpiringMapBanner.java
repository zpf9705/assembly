package top.osjf.assembly.cache.autoconfigure;

/**
 * {@link net.jodah.expiringmap.ExpiringMap} Print relevant information in the form of a logo to the console.
 *
 * @author zpf
 * @since 1.0.0
 */
class ExpiringMapBanner implements StartUpBanner {

    private static final String BANNER = "" +
            "                 _                                  \n" +
            "  _____  ___ __ (_)_ __ ___   _ __ ___   __ _ _ __  \n" +
            " / _ \\ \\/ / '_ \\| | '__/ _ \\ | '_ ` _ \\ / _` | '_ \\ \n" +
            "|  __/>  <| |_) | | | |  __/ | | | | | | (_| | |_) |\n" +
            " \\___/_/\\_\\ .__/|_|_|  \\___| |_| |_| |_|\\__,_| .__/ \n" +
            "          |_|                                |_|    ";


    private static final String EXPIRE_MAP_SINE = " :: Expire Map :: ";

    @Override
    public String getBanner() {
        return BANNER;
    }

    @Override
    public String getLeftSign() {
        return EXPIRE_MAP_SINE;
    }
}
