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

package top.osjf.sdk.spring.proxy;

import org.springframework.beans.factory.FactoryBean;
import top.osjf.sdk.core.client.ClientExecutors;
import top.osjf.sdk.spring.EnableSdkProxyRegister;
import top.osjf.sdk.spring.Sdk;
import top.osjf.sdk.spring.beans.ScanningCandidateImportBeanDefinitionRegistrar;

/**
 * This class contains all the information about the composition of the SDK,
 * including the host address, the base of the Spring proxy bean implementation
 * class, and the final parameter convergence point of the JDK method dynamic
 * proxy.
 *
 * <p>It can be said to be a fusion point between the Spring framework and
 * our custom {@link top.osjf.sdk.core.client.Client} scheme.
 * <p>Here is an explanation of the main implementation idea: we first scan the
 * interface classes wearing {@link EnableSdkProxyRegister} in
 * {@link ScanningCandidateImportBeanDefinitionRegistrar} through{@link Sdk}'s
 * switch annotations,and then create dynamic implementation classes for these
 * interfaces through {@link top.osjf.sdk.spring.SdkProxyBeanRegister}.
 *
 * <p>At this point, the implementation class is given to the proxy objects
 * created by the jdk dynamic proxy, and the proxy objects are handed over
 * to spring as the virtual implementation classes for these interfaces.
 *
 * <p>When these interfaces are called through the spring container, We will
 * uniformly bring the parameters to the proxy object and connect them to our
 * {@link ClientExecutors} processing through this class.
 *
 * <p>For clearer meaning, it was renamed 'SdkJDKProxyBean', which
 * means that this class implements JDK dynamic proxy to create objects.
 *
 * <p>When setting the scope of a special bean and inheriting this class, it is
 * necessary to override the constructor {@link #SdkJDKProxyBean(Class)} and provide
 * the corresponding type for setting {@link FactoryBean#getObjectType()}.
 *
 * @param <T> The data type of the proxy class.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class SdkJDKProxyBean<T> extends AbstractSdkProxyBean<T> {

    /**
     * The construction method for setting the proxy model {@link ProxyModel} to cglib.
     */
    public SdkJDKProxyBean() {
        super();
        setProxyModel(ProxyModel.JDK);
    }

    public SdkJDKProxyBean(Class<T> type) {
        super(type);
        setProxyModel(ProxyModel.JDK);
    }
}
