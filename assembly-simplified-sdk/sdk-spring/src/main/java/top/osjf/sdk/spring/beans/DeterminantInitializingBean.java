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

package top.osjf.sdk.spring.beans;

import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import top.osjf.sdk.commons.annotation.NotNull;

/**
 * Interface to be implemented by beans that need to react once all their properties
 * have been set by a {@link org.springframework.beans.factory.BeanFactory}: e.g. to perform custom initialization,
 * or merely to check that all mandatory properties have been set.
 *
 * <p>An alternative to implementing {@code InitializingBean} is specifying a custom
 * init method, for example in an XML bean definition. For a list of all bean
 * lifecycle methods, see the {@link org.springframework.beans.factory.BeanFactory BeanFactory javadocs}.
 *
 * <p><span>The above fixation is copied from {@link org.springframework.beans.factory.InitializingBean}.
 * </span>
 *
 * <p>Dynamically retrieve this interface from the container, match it with the specified
 * type of {@link #getType()}, and perform the corresponding initialization operation at
 * time {@link InitializingBean#afterPropertiesSet()}.
 *
 * <p>When specifying multiple {@link DeterminantInitializingBean}, if you want to specify the
 * execution order, you can use the concept of {@link org.springframework.core.annotation.Order}
 * to specify it. Support for it has been added because the implementation class of this
 * interface requires adding a Spring container to take effect.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see org.springframework.beans.factory.InitializingBean
 * @since 1.0.0
 */
public interface DeterminantInitializingBean {

    /**
     * @return Return the type of proxy service required.
     */
    @NotNull
    Class<?> getType();

    /**
     * Copy from {@link org.springframework.beans.factory.InitializingBean}.
     * Invoked by the containing {@code BeanFactory} after it has set all bean properties
     * and satisfied {@link BeanFactoryAware}, {@code ApplicationContextAware} etc.
     * <p>This method allows the bean instance to perform validation of its overall
     * configuration and final initialization when all bean properties have been set.
     *
     * @throws Exception in the event of misconfiguration (such as failure to set an
     *                   essential property) or if initialization fails for any other reason
     */
    void afterPropertiesSet() throws Exception;
}
