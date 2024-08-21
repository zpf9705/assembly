/*
 * Copyright 2024-? the original author or authors.
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

package top.osjf.cron.core.lifestyle;

import top.osjf.cron.core.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The StartupMetadata interface defines a method for retrieving startup arguments.
 *
 * <p>This interface encapsulates the information about the arguments that need to be received
 * during application startup. By implementing this interface, the startup arguments can be
 * easily accessed across different components of the application, enabling flexible
 * configuration and initialization logic.</p>
 *
 * <p>Key Methods:</p>
 * <ul>
 *     <li><b>{@link #getStartUpArgs()}</b>: Returns an array of startup arguments.
 *     These arguments may come from command line, configuration files, environment variables,
 *     etc.</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.1
 */
public interface StartupMetadata {

    /**
     * Retrieves the array of arguments passed during application startup.
     *
     * <p>This method returns an array of objects containing the arguments passed when the
     * application is started. The specific content and format of these arguments depend on
     * how the application receives and parses them. They may come from various sources such
     * as command line input, configuration files, environment variables, etc.</p>
     *
     * <p>The returned array may be empty, indicating that no startup arguments were provided.</p>
     *
     * @return The array of startup arguments, which may be empty.
     */
    Object[] getStartUpArgs();

    /**
     * Extends the StartupMetadata interface to add a single startup argument.
     *
     * <p>This method allows adding a single argument to the collection of startup arguments for
     * the application. It can be used to dynamically collect or modify startup arguments during
     * the application startup process.</p>
     *
     * @param arg The startup argument to add.
     */
    void addStartupArg(Object arg);

    /**
     * Adds multiple startup arguments.
     *
     * <p>This method allows adding multiple startup arguments to the collection of startup arguments
     * for the application in a single call. It accepts a varargs (variable arguments) array, iterates
     * over the array, and adds each argument to the collection of startup arguments.</p>
     *
     * <p>If the passed argument array is null or has a length of 0, this method does not perform
     * any operation.</p>
     *
     * @param args The array of startup arguments to add, which can be null or have a length of 0.
     */
    void addStartupArgs(Object... args);

    /**
     * Static factory method for {@link StartupMetadata} to create a
     * default {@link DefaultStartupMetadata}.
     *
     * @param args startup args.
     * @return a default {@link StartupMetadata} instance.
     */
    static StartupMetadata of(Object... args) {
        return new DefaultStartupMetadata(args);
    }

    /**
     * The default impl for {@link StartupMetadata}.
     */
    class DefaultStartupMetadata implements StartupMetadata {

        private final List<Object> listArgs = new ArrayList<>();

        public DefaultStartupMetadata(Object[] args) {
            addStartupArgs(args);
        }

        @Override
        public Object[] getStartUpArgs() {
            return listArgs.toArray();
        }

        @Override
        public void addStartupArg(Object arg) {
            if (arg != null) {
                listArgs.add(arg);
            }
        }

        @Override
        public void addStartupArgs(Object... args) {
            if (!ArrayUtils.isEmpty(args)) {
                listArgs.addAll(Arrays.asList(args));
            }
        }
    }
}