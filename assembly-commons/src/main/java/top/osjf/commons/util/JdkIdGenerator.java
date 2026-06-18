
package top.osjf.commons.util;

import java.util.UUID;

/**
 * This class was copied from {@code org.springframework.core}, with minor modifications
 * and adaptations. I would like to express my sincere gratitude here!
 *
 * An {@link IdGenerator} that calls {@link UUID#randomUUID()}.
 */
public class JdkIdGenerator implements IdGenerator {

	@Override
	public UUID generateId() {
		return UUID.randomUUID();
	}

}
