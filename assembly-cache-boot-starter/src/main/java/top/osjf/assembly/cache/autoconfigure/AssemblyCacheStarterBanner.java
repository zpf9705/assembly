package top.osjf.assembly.cache.autoconfigure;

/**
 * The collection class for assembly cache logo output information, including its logo string
 * and identifier, is output to the console when applying automatic assembly.
 *
 * @author zpf
 * @since 1.0.0
 */
class AssemblyCacheStarterBanner implements StartUpBanner {

    private static final String BANNER = "" +
            "    _                           _     _                        _          \n" +
            "   / \\   ___ ___  ___ _ __ ___ | |__ | |_   _    ___ __ _  ___| |__   ___ \n" +
            "  / _ \\ / __/ __|/ _ \\ '_ ` _ \\| '_ \\| | | | |  / __/ _` |/ __| '_ \\ / _ \\\n" +
            " / ___ \\\\__ \\__ \\  __/ | | | | | |_) | | |_| | | (_| (_| | (__| | | |  __/\n" +
            "/_/   \\_\\___/___/\\___|_| |_| |_|_.__/|_|\\__, |  \\___\\__,_|\\___|_| |_|\\___|\n" +
            "                                        |___/                             ";


    private static final String EXPIRE_STARTERS_SINE = " :: Assembly Cache :: ";

    @Override
    public String getBanner() {
        return BANNER;
    }

    @Override
    public String getLeftSign() {
        return EXPIRE_STARTERS_SINE;
    }
}
