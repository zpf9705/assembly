
package top.osjf.commons.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class was copied from {@code org.springframework.core}, with minor modifications
 * and adaptations. I would like to express my sincere gratitude here!
 *
 * A simple {@link IdGenerator} that starts at 1, increments up to
 * {@link Long#MAX_VALUE}, and then rolls over.
 */
public class SimpleIdGenerator implements IdGenerator {

	private final AtomicLong leastSigBits = new AtomicLong();


	@Override
	public UUID generateId() {
		return new UUID(0, this.leastSigBits.incrementAndGet());
	}

}
