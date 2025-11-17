/*
 * Copyright 2025-? the original author or authors.
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


package top.osjf.cron.datasource.driven.scheduled.serialization;

import com.thoughtworks.xstream.XStream;
import top.osjf.cron.core.lang.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Default XML serializer for {@link ConfigurableTaskElement} list.
 *
 * <p>This class provides serialization and deserialization between a list of {@link ConfigurableTaskElement}
 * objects and XML format, using the XStream library. It defines clear XML structure with aliases to ensure
 * human-readable and maintainable configuration files.
 *
 * <h3>Supported XML Format:</h3>
 * The serializer supports the following standard XML structure:
 *
 * <pre>
 * &lt;elements&gt;
 *   &lt;element&gt;
 *     &lt;taskName&gt;checkHealth&lt;/taskName&gt;
 *     &lt;interval&gt;60000&lt;/interval&gt;
 *     &lt;enabled&gt;true&lt;/enabled&gt;
 *     &lt;!-- Additional fields are mapped based on ConfigurableTaskElement properties --&gt;
 *   &lt;/element&gt;
 *   &lt;element&gt;
 *     &lt;taskName&gt;syncData&lt;/taskName&gt;
 *     &lt;interval&gt;30000&lt;/interval&gt;
 *     &lt;enabled&gt;false&lt;/enabled&gt;
 *   &lt;/element&gt;
 * &lt;/elements&gt;
 * </pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class DefaultXmlConfigTaskElementSerializer implements ConfigTaskElementSerializer {

    /**
     * Root element tag name in the generated or parsed XML document.
     * Represents the container of multiple task elements.
     */
    private static final String ROOT = "elements";

    /** Child element tag name representing a single {@link ConfigurableTaskElement}.*/
    private static final String NODE = "element";

    /**
     * The XStream instance used for object-to-XML and XML-to-object conversion.
     * Configured with type permissions, aliases, and reference mode.
     */
    private final XStream xstream;

    /**
     * Constructs a new {@code DefaultXmlConfigTaskElementSerializer} to init a {@link XStream}
     * with any default settings.
     */
    public DefaultXmlConfigTaskElementSerializer() {
        xstream = new XStream();
        xstream.allowTypes(new Class[]{ ArrayList.class, ConfigurableTaskElement.class });
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias(ROOT, List.class);
        xstream.alias(NODE, ConfigurableTaskElement.class);
    }

    /**
     * Constructs a new {@code DefaultXmlConfigTaskElementSerializer} by given {@link XStream}.
     * @param xstream the given {@link XStream}.
     */
    public DefaultXmlConfigTaskElementSerializer(XStream xstream) {
        this.xstream = xstream;
    }

    @Override
    public String serialize(@NotNull List<ConfigurableTaskElement> elements) {
        return xstream.toXML(elements);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ConfigurableTaskElement> deserialize(@NotNull String configInfo) {
        return (List<ConfigurableTaskElement>) xstream.fromXML(configInfo);
    }

    @Override
    public ConfigFormat getConfigFormat() {
        return ConfigFormat.XML;
    }
}
