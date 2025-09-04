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

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.datasource.driven.scheduled.DataSourceDrivenException;
import top.osjf.cron.datasource.driven.scheduled.external.file.ExternalFileTaskElementLoader;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

/**
 * The Yaml loader for {@link YamlTaskElement} loading.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class YamlTaskElementLoader extends ExternalFileTaskElementLoader<YamlTaskElement> {

    /** Default config file named task-config.yml */
    protected static final String DEFAULT_CONFIG_FILE_NAME = "task-config.yml";

    private Yaml yaml;

    /**
     * Constructs an empty {@code YamlTaskElementLoader} and init a {@link Yaml} instance.
     */
    public YamlTaskElementLoader() {
        this.yaml = buildDefaultYaml();
    }

    /**
     * Build a default {@link Yaml} instance.
     *
     * <p>Includes the following configuration items for {@link Yaml}:
     * <ul>
     *     <li>Configure the default data flow style as block.{@link DumperOptions.FlowStyle#BLOCK}</li>
     *     <li>First line locked in 2</li>
     *     <li>Optimize output</li>
     *     <li>Optimize output</li>
     *     <li>Maintain the format of the data itself without outputting the label of intermediary
     *     type {@link YamlTaskElement}.</li>
     * </ul>
     *
     * <p>So if there is no custom {@link Yaml} and the method {@link #setYaml(Yaml)} is called for
     * setting, the default format that needs to be given is as follows:
     * <ul>
     * <li>- expression: 0/1 * * * * ?</li>
     * <li>&nbsp&nbsp id: '3'</li>
     * <li>&nbsp&nbsp profiles: local </li>
     * <li>&nbsp&nbsp status: ACTIVE</li>
     * <li>&nbsp&nbsp statusDescription: ACTIVE => Running</li>
     * <li>&nbsp&nbsp taskDescription: xxx</li>
     * <li>&nbsp&nbsp taskId: xxx</li>
     * <li>&nbsp&nbsp taskName: '@myTask1.doTask()'</li>
     * <li>&nbsp&nbsp updateSign: 0</li>
     *
     * <li>- expression: 0/1 * * * * ?</li>
     * <li>&nbsp&nbsp id: '3'</li>
     * <li>&nbsp&nbsp profiles: local </li>
     * <li>&nbsp&nbsp status: ACTIVE</li>
     * <li>&nbsp&nbsp statusDescription: ACTIVE => Running</li>
     * <li>&nbsp&nbsp taskDescription: xxx</li>
     * <li>&nbsp&nbsp taskId: xxx</li>
     * <li>&nbsp&nbsp taskName: '@myTask2.doTask()'</li>
     * <li>&nbsp&nbsp updateSign: 0</li>
     * </ul>
     *
     * <p>The default format file is in
     * {@literal top/osjf/cron/datasource/driven/scheduled/yaml/DefaultFormatExample.yml}.
     *
     * @return A default {@link Yaml} instance.
     */
    private static Yaml buildDefaultYaml() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setIndent(2);
        dumperOptions.setPrettyFlow(true);
        Representer representer = new Representer();
        representer.addClassTag(YamlTaskElement.class, Tag.MAP);
        return new Yaml(representer, dumperOptions);
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
    protected void refresh() {
        try (Writer writer = new FileWriter(getConfigFile())) {
            yaml.dump(taskElements, writer);
        }
        catch (Throwable ex) {
            throw new DataSourceDrivenException("Failed to dump file : " + getConfigFile().getPath(), ex);
        }
    }

    @Override
    protected List<YamlTaskElement> loadingInternal(InputStream is) {
        @SuppressWarnings("unchecked")
        List<YamlTaskElement> loadingResult = yaml.loadAs(is, List.class);
        return loadingResult == null ? Collections.emptyList() : loadingResult;
    }

    @Override
    protected String defaultConfigFileName() {
        return DEFAULT_CONFIG_FILE_NAME;
    }
}
