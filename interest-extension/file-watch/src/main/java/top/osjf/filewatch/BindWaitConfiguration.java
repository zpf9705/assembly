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
 * Bind the configuration {@code WaitConfiguration} wrapper of the specific parent
 * path and corresponding child file path as a reference class for registration.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class BindWaitConfiguration implements Comparable<BindWaitConfiguration>, Serializable {

    private static final long serialVersionUID = -7383993053567660771L;

    /** the parent directory path.*/
    private String bindPath;

    /** the context path.*/
    private String pathContext;

    /** The configuration for waiting for the completion of the listening file operation.*/
    private WaitConfiguration configuration;

    public BindWaitConfiguration() {
    }

    public BindWaitConfiguration(String bindPath, String pathContext, WaitConfiguration configuration) {
        this.bindPath = bindPath;
        this.pathContext = pathContext;
        this.configuration = configuration;
    }

    @Override
    public int compareTo(BindWaitConfiguration o) {
        int bindPathComparison = bindPath.compareTo(o.bindPath);
        if (bindPathComparison == 0) {
            return pathContext.compareTo(o.pathContext);
        }
        return bindPathComparison;
    }

    @Override
    public String toString() {
        return bindPath + "@" + pathContext;
    }

    public String getBindPath() {
        return bindPath;
    }

    public void setBindPath(String bindPath) {
        this.bindPath = bindPath;
    }

    public String getPathContext() {
        return pathContext;
    }

    public void setPathContext(String pathContext) {
        this.pathContext = pathContext;
    }

    public WaitConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(WaitConfiguration configuration) {
        this.configuration = configuration;
    }
}
