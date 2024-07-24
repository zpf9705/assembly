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

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.lang.NonNull;

/**
 * Interface to be implemented by beans that want to release resources on destruction.
 * A {@link BeanFactory} will invoke the destroy method on individual destruction of a
 * scoped bean. An {@link org.springframework.context.ApplicationContext} is supposed
 * to dispose all of its singletons on shutdown, driven by the application lifecycle.
 *
 * <p>A Spring-managed bean may also implement Java's {@link AutoCloseable} interface
 * for the same purpose. An alternative to implementing an interface is specifying a
 * custom destroy method, for example in an XML bean definition. For a list of all
 * bean lifecycle methods, see the {@link BeanFactory BeanFactory javadocs}.
 *
 * <p><span>The above fixation is copied from {@link org.springframework.beans.factory.DisposableBean}.
 * </span>
 *
 * <p>Dynamically retrieve this interface from the container, match it with the
 * specified type of {@link #getType()}, and perform the corresponding destruction
 * operation at time {@link DisposableBean#destroy()}.
 *
 * <p>When specifying multiple {@link DeterminantDisposableBean}, if you want to specify the
 * execution order, you can use the concept of {@link org.springframework.core.annotation.Order}
 * to specify it. Support for it has been added because the implementation class of this
 * interface requires adding a Spring container to take effect.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see org.springframework.beans.factory.DisposableBean
 * @since 1.0.0
 */
public interface DeterminantDisposableBean {

    /**
     * @return Return the type of proxy service required.
     */
    @NonNull
    Class<?> getType();

    /**
     * Copy from {@link org.springframework.beans.factory.DisposableBean}.
     * Invoked by the containing {@code BeanFactory} on destruction of a bean.
     *
     * @throws Exception in case of shutdown errors. Exceptions will get logged
     *                   but not rethrown to allow other beans to release their resources as well.
     */
    void destroy() throws Exception;
}
