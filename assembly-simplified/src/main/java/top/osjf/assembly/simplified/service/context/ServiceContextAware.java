/*
 * Copyright 2023-2024 the original author or authors.
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

package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * Interface to be implemented by any bean that wishes to be
 * notified of the {@link ServiceContext} that it runs in.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.1
 */
public interface ServiceContextAware extends Aware {

    /**
     * Set the ServiceContext that this object runs in.
     * <p>Invoked after population of normal bean properties but before an init
     * callback like InitializingBean's afterPropertiesSet or a custom init-method.
     *
     * @param serviceContext service context to be used by this object
     * @throws BeansException if thrown by service context methods.
     */
    void setServiceContext(@NotNull ServiceContext serviceContext) throws BeansException;
}
