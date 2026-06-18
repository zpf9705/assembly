
package top.osjf.commons.util;

import java.util.UUID;

/**
 * This class was copied from {@code org.springframework.core}, with minor modifications
 * and adaptations. I would like to express my sincere gratitude here!
 *
 * Contract for generating universally unique identifiers ({@link UUID UUIDs}).
 */
@FunctionalInterface
public interface IdGenerator {

	/**
	 * Generate a new identifier.
	 * @return the generated identifier
	 */
	UUID generateId();

}
