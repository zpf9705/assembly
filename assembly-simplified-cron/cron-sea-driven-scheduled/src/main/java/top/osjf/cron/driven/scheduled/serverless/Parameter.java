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


package top.osjf.cron.driven.scheduled.serverless;

import java.lang.annotation.*;

/**
 * Annotation for marking fields as task execution parameters.
 * Supports custom parameter names and provides object-to-string serialization strategy
 * for non-String type fields.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Parameter {
    /**
     * Defines the name of the parameter.
     * <p>If not set (empty string by default), the framework will use the field name
     * as the parameter name.
     * @return custom parameter name (field name if empty)
     */
    String name() default "";

    /**
     * Specifies the strategy for serializing objects to Strings.
     * <p>This strategy takes effect ONLY when the field type is NOT {@code String}, used
     * to convert the field object into a String value. A default implementation
     * {@code SimpleObjectToStringSerializationStrategy} is provided. Users can customize
     * the strategy by implementing {@code ObjectToStringSerializationStrategy}.
     *
     * @return serialization strategy {@code ObjectToStringSerializationStrategy} class
     */
    Class<? extends ObjectToStringSerializationStrategy> serializationStrategy()
            default SimpleObjectToStringSerializationStrategy.class;

    /**
     * Define the type {@link Type} of this series of parameters, which defaults to the
     * application parameter {@link Type#APPLICATION}. Indicate the necessary parameters
     * or custom application parameter types that must be configured when launching
     * application functions.
     * @return the type {@link Type} of this series of parameters.
     */
    Type type() default Type.APPLICATION;

    /**
     * An enumeration class that defines parameter types.
     *
     * <p>According to the placement of parameters in the Jar package startup command,
     * the type of parameters should be specified in accordance with the following relevant
     * specifications:
     * <p><strong>{@code java
     * [JVM Parameters (-keyvalue(JVM built-in parameter: - keyvalue has no equal sign)/
     * -Dkey=value(JVM system attribute: - Dkey=value has an equal sign))]
     * -jar [Jar Package Path]
     * [Application Parameters (--key=value)]}</strong>
     *
     * <p>To give a specific example:
     * <pre>
     *     {@code
     *     public class MyParameter {
     *         Parameter(name = "Xmx", type = Type.JVM)
     *         private String xmx = "2g";
     *
     *         Parameter(name = "Xms", type = Type.JVM)
     *         private String xms = "2g";
     *
     *         Parameter(name = "Duser.dir", type = Type.JVM)
     *         private String userDir = '/local/user';
     *
     *         Parameter(name = "server.port", type = Type.APPLICATION)
     *         private Integer serverPort = 8080;
     *     }
     *    }
     * </pre>
     * The formatting command is: <strong>java -Xmx2g -Xms2g -Duser.dir=/local/user xxx.jar --server.port=8080</strong>
     */
    enum Type {

        /**
         * This type indicates that the parameters passed are acceptable parameter group types
         * for JVM internal tuning, such as {@code -XX:+UseG1GC -Xmx2g -Xms2g}.
         */
        JVM,

        /**
         * This type indicates that the parameters passed are acceptable parameter group types
         * for internal configuration of the application, for example {@code --server.port=8080}.
         */
        APPLICATION
    }
}
