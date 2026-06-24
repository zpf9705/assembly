/*
 * Copyright 2026-? the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package top.osjf.commons.ability;

/**
 * {@link Nameable} implemented based on the JVM native object identity hash code.
 *
 * <p><b>Important Risk Note:</b></p>
 * <ul>
 * <li>Using {@link System#identityHashCode(Object)} can avoid the problem of custom {@code hashCode()}
 * being rewritten;</li>
 * <li>The return value will change after the application restarts or a new instance is created,
 * which will lead to the splitting of monitoring metrics and cannot be used for persistent or aggregated
 * business scenarios;</li>
 * <li>It is only suitable for local temporary debugging scenarios, not recommended for online formal
 * business use.</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class HashCodeNameable implements Nameable {

    private final String name;

    public HashCodeNameable() {
        this.name = String.valueOf(System.identityHashCode(this));
    }

    @Override
    public String getName() {
        return name;
    }
}
