package top.osjf.assembly.cache.factory;

import top.osjf.assembly.cache.util.CodecUtils;
import top.osjf.assembly.util.ByteContain;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Abstract cache executor classes , link the abstract template for {@link CacheExecutor}.<br>
 * Provide some check on cache the implementer, tips and some other help category.
 * <p>
 * To provide an interface can extend the cache class.<br>
 * And global unified configuration byte [] types of cache model.<br>
 * Can be by a specific method to obtain the corresponding help center.<br>
 * <ul>
 *     <li>{@link HelpCenter}</li>
 *     <li>{@link HelpCenter#getHelpCenter()}</li>
 *     <li>{@link HelpCenter#getContain()}</li>
 * </ul>
 *
 * @author zpf
 * @since 1.0.0
 */
public abstract class AbstractCacheExecutor<T> implements DefaultCacheExecutor {

    private final HelpCenter<T> helpCenter;

    public AbstractCacheExecutor(@NotNull HelpCenter<T> helpCenter) {
        this.helpCenter = helpCenter;
    }

    public T getHelpCenter() {
        return this.helpCenter.getHelpCenter();
    }

    public ByteContain contain() {
        return this.helpCenter.getContain();
    }

    /**
     * Similar object {@code key} and object {@code value}  check function expression.
     */
    private final BiFunction<Object, Object, Boolean> compare = (b, c) -> {
        if (b != null && c != null) {
            Predicate<String> predicate = CodecUtils.findPredicate(
                    CodecUtils.toStingBeReal(c)
            );
            return predicate.test(CodecUtils.toStingBeReal(b));
        }
        return false;
    };

    /**
     * Compare with {@code byte[]} of similar.
     *
     * @param compare  must not be {@literal null}.
     * @param compare_ must not be {@literal null}.
     * @return if {@literal true} prove that similar
     */
    public boolean similarJudgeOfBytes(byte[] compare, byte[] compare_) {
        return this.compare.apply(compare, compare_);
    }
}
