
package top.osjf.commons.util.function;

import top.osjf.commons.lang.Nullable;

import java.util.function.Supplier;

/**
 * This class was copied from {@code org.springframework.core}, with minor modifications
 * and adaptations. I would like to express my sincere gratitude here!
 *
 * Convenience utilities for {@link Supplier} handling.
 */
public abstract class SupplierUtils {

	/**
	 * Resolve the given {@code Supplier}, getting its result or immediately
	 * returning {@code null} if the supplier itself was {@code null}.
	 * @param supplier the supplier to resolve
	 * @return the supplier's result, or {@code null} if none
	 */
	@Nullable
	public static <T> T resolve(@Nullable Supplier<T> supplier) {
		return (supplier != null ? supplier.get() : null);
	}

}
