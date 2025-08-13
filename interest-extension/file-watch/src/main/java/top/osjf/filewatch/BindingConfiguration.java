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


package top.osjf.filewatch;

import java.io.Serializable;

/**
 * The abstract config {@code BindingConfiguration} that analysis and retrieval
 * of various specific configurations (such as {@link WaitConfiguration}) based
 * on the specified context path {@link #getPathContext()} under the bound parent
 * path.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public abstract class BindingConfiguration
        extends TriggerKindProvider implements Comparable<BindingConfiguration>, Serializable {

    private static final long serialVersionUID = -924081876676893016L;

    /** the parent directory path.*/
    private String bindPath;

    /** The configuration for waiting for the completion of the listening file operation. */
    private WaitConfiguration configuration = new WaitConfiguration();

    /**
     * Define the sub level as an abstract method and provide clearer contextual
     * definitions for subclasses.
     * @return the path context defined by the subclass.
     */
    public abstract String getPathContext();

    @Override
    public int compareTo(BindingConfiguration o) {
        int bindPathComparison = bindPath.compareTo(o.bindPath);
        if (bindPathComparison == 0) {
            return getPathContext().compareTo(o.getPathContext());
        }
        return bindPathComparison;
    }

    @Override
    public String toString() {
        return bindPath + "@" + getPathContext();
    }

    public String getBindPath() {
        return bindPath;
    }

    public void setBindPath(String bindPath) {
        this.bindPath = bindPath;
    }

    public WaitConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(WaitConfiguration configuration) {
        this.configuration = configuration;
    }
}
