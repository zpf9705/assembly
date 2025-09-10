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


package top.osjf.optimize.aware;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;

/**
 * A generic-aware interface that extends Spring's {@link Aware} marker interface
 * to provide type-safe dependency injection callback mechanism for specific bean
 * types.
 *
 * <p>This interface follows the Spring container's callback pattern and is typically
 * implemented by components that need to be aware of and obtain references to specific
 * bean instances during initialization phase.
 *
 * <p><strong>Important Restrictions:</strong>
 * <ul>
 *   <li><b>Container infrastructure types (ApplicationContext/BeanFactory) are NOT supported</b></li>
 *   <li>For container access, implement {@link org.springframework.context.ApplicationContextAware}
 *       or {@link org.springframework.beans.factory.BeanFactoryAware} instead</li>
 *   <li>Generic type {@code T} must represent an application-managed bean</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Basic Implementation</h3>
 * <pre class="code">
 * // 1. Define application bean
 * public class PaymentService {
 *     public void processPayment() {
 *         // business logic
 *     }
 * }
 *
 * // 2. Implement BeanAware correctly
 * public class PaymentProcessor implements BeanAware&lt;PaymentService&gt; {
 *     private PaymentService service;
 *
 *     {@literal @}Override
 *     public void setBean(PaymentService service) {
 *         this.service = service;
 *     }
 *
 *     public void executePayment() {
 *         service.processPayment();
 *     }
 * }</pre>
 *
 * <h3>Spring Configuration</h3>
 * <pre class="code">
 * {@literal @}Configuration
 * public class AppConfig {
 *     {@literal @}Bean
 *     public PaymentService paymentService() {
 *         return new PaymentService();
 *     }
 *
 *     {@literal @}Bean
 *     public PaymentProcessor paymentProcessor() {
 *         return new PaymentProcessor();
 *     }
 * }</pre>
 *
 * <h3>Forbidden Case</h3>
 * <p><span style="color:red">Warning:</span> This will cause BeanCreationException
 * <pre class="code">
 * // ❌ Violates restriction: Using container interface
 * public class InvalidImpl implements BeanAware&lt;ApplicationContext&gt; {
 *     public void setBean(ApplicationContext ctx) { ... }
 * }</pre>
 *
 * @param <T> the type of bean that the implementing class should be aware of.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 *
 * @see BeanAwareSupportBeanPostProcessor
 * @see org.springframework.context.ApplicationContextAware
 * @see org.springframework.beans.factory.BeanFactoryAware
 */
public interface BeanAware<T> extends Aware {

    /**
     * Callback method that supplies the bean instance of the specified type to the
     * implementing class.
     *
     * <p>This method is invoked by the Spring container during bean initialization
     * phase,following the dependency injection lifecycle. The injected bean is guaranteed
     * to be fully initialized and ready for use, conforming to the container's singleton
     * scope management rules.
     *
     * @param bean the bean instance to be injected, never {@code null}.
     * @throws BeansException if a specified bean perception failure error occurs.
     */
    void setBean(T bean) throws BeansException;
}
