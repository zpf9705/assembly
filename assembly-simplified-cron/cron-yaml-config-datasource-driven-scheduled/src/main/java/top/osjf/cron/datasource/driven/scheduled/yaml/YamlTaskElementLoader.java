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


package top.osjf.cron.datasource.driven.scheduled.yaml;

import org.yaml.snakeyaml.Yaml;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.datasource.driven.scheduled.DataSourceDrivenException;
import top.osjf.cron.datasource.driven.scheduled.external.file.ExternalFileTaskElementLoader;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Yaml loader for {@link YamlTaskElement} loading.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class YamlTaskElementLoader extends ExternalFileTaskElementLoader<YamlTaskElement> {

    /** Default config file named task-config.yml */
    private static final String DEFAULT_CONFIG_FILE_NAME = "task-config.yml";

    /** The map of loading result. */
    private Map<Object, Map<Object, Object>> loadingResult;

    private Yaml yaml;

    /**
     * Constructs an empty {@code YamlTaskElementLoader} and init a {@link Yaml} instance.
     */
    public YamlTaskElementLoader() {
        this.yaml = new Yaml();
    }

    /**
     * Sets the Yaml instance to be used for parsing and serialization.
     *
     * @param yaml The Yaml instance to set. This instance will be used for YAML
     *             parsing and serialization operations.
     */
    public void setYaml(@NotNull Yaml yaml) {
        this.yaml = yaml;
    }

    @Override
    protected boolean purgeInternal(YamlTaskElement taskElement) {
        return taskElement.purge();
    }

    @Override
    protected void updateInternal(YamlTaskElement updateElement) {
        Object sourceId
                = updateElement.getSourceYamlConfig(YamlTaskElement.ID_KEY_NAME);
        loadingResult.put(sourceId, updateElement.getSourceYamlConfig());
    }

    @Override
    protected void refresh() {
        try (Writer writer = new FileWriter(getConfigFile())) {
            yaml.dump(loadingResult, writer);
        }
        catch (Throwable ex) {
            throw new DataSourceDrivenException("Failed to dump file : " + getConfigFile().getName(), ex);
        }
    }

    @Override
    protected List<YamlTaskElement> loadingInternal(InputStream is) {
        loadingResult = yaml.load(is);
        return loadingResult == null ? Collections.emptyList() : loadingResult.values().stream()
                .map(YamlTaskElement::new).collect(Collectors.toList());
    }

    @Override
    protected String defaultConfigFileName() {
        return DEFAULT_CONFIG_FILE_NAME;
    }
}
