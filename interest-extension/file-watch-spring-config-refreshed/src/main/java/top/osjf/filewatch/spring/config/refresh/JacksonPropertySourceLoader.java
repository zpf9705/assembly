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


package top.osjf.filewatch.spring.config.refresh;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Strategy to load '.json' files into a {@link PropertySource}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class JacksonPropertySourceLoader implements PropertySourceLoader {

    private static final Logger logger = LoggerFactory.getLogger(JacksonPropertySourceLoader.class);

    private static final String[] FILE_EXTENSIONS = new String[]{"json"};

    private final ObjectMapper objectMapper;

    /**
     * Constructs an empty {@code JacksonPropertySourceLoader} to give a default {@link ObjectMapper}.
     */
    public JacksonPropertySourceLoader() {
        this(new ObjectMapper());
    }

    /**
     * Constructs a {@code JacksonPropertySourceLoader} with the given {@link ObjectMapper}.
     * @param objectMapper the given {@code ObjectMapper}.
     */
    public JacksonPropertySourceLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String[] getFileExtensions() {
        return FILE_EXTENSIONS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        if (!ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", getClass().getClassLoader())) {
            throw new IllegalStateException(
                    "Attempted to load " + name + " but jackson.ObjectMapper was not found on the classpath");
        }
        if (!resource.exists()) {
            return Collections.emptyList();
        }

        Map<String, Object> source;
        try (InputStream inputStream = resource.getInputStream()) {
            source = objectMapper.readValue(inputStream, Map.class);
        }
        catch (IOException ex) {
            logger.error("Failed to load file <" + resource.getFile().getPath() +"> using the Jackson loader.", ex);
            throw ex;
        }
        return Collections.singletonList(new OriginTrackedMapPropertySource(name, source, true));
    }
}
