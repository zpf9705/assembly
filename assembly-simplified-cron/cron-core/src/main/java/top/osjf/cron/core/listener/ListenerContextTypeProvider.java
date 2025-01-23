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


package top.osjf.cron.core.listener;

import java.lang.annotation.*;

/**
 * Annotation {@code @ ListenerContextTypeProvider} is used to mark a class, indicating that
 * the class provides a specific type of {@code ListenerContext} implementation.
 * <p>
 * This annotation is typically used within the framework to dynamically discover and process
 * these {@code ListenerContext} implementations through reflection mechanisms at runtime.
 * The class marked by this annotation should be able to provide an implementation class for
 * {@code ListenerContext}, which typically contains the listener context information required
 * for the framework to run.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ListenerContextTypeProvider {

    /**
     * Return the Class object of the class that implements the {@code ListenerContext}
     * interface.
     *
     * <p>This method should return a Class object pointing to a class that implements the
     * {@code ListenerContext} interface.
     * This class typically contains specific listener context information required by the
     * framework and is dynamically loaded and used by the framework at runtime.
     *
     * @return Implemented the Class object of the {@code ListenerContext} interface's class.
     */
    Class<? extends ListenerContext> value();

    /**
     * Return the context object of the original task to participate in the enumeration model
     * {@link BuildMode} constructed by the listening context object {@link ListenerContext}
     * specified by {@link #value()}.
     *
     * @return The build model of the original context object participates in listening context
     * construction.
     */
    BuildMode sourceContextBuildMode() default BuildMode.CONSTRUCTOR;


    /**
     * This enumeration describes the way in which the context information object of the
     * original task participates in the construction of the listening context object.
     */
    enum BuildMode {

        /**
         * Participate in the form of constructing method parameters.
         */
        CONSTRUCTOR,

        /**
         * Participate in the form of set method settings.
         */
        SET
    }
}
