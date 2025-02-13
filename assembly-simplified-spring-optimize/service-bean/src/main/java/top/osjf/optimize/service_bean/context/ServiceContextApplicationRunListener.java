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


package top.osjf.optimize.service_bean.context;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ReflectionUtils;
import top.osjf.optimize.service_bean.annotation.EnableServiceCollection;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * The {@code ServiceContextApplicationRunListener} class implements the
 * {@code SpringApplicationRunListener} interface and has its required
 * constructor method {@link #ServiceContextApplicationRunListener}.
 *
 * <p>This class is mainly used to detect whether the {@link EnableServiceCollection}
 * annotation exists in the package and its parent package where the Spring
 * main application class is located. If the annotation exists, a custom
 * {@link ServiceContextBeanNameGenerator} will be set in the Spring application
 * context to meet the component naming requirements of the current framework.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class ServiceContextApplicationRunListener implements SpringApplicationRunListener {

    /**
     * The main class for launching the Spring framework.
     */
    private final Class<?> mainApplicationClass;

    /**
     * The necessary constructor for implements {@link SpringApplicationRunListener}.
     *
     * @param application spring's application startup instance.
     * @param args        the startup parameters for the application startup class of spring.
     */
    public ServiceContextApplicationRunListener(SpringApplication application, String[] args) {
        mainApplicationClass = application.getMainApplicationClass();
    }

    /**
     * Returns whether the current project indicates a service annotation.
     *
     * @return If {@code true} the current project indicates a service annotation,otherwise not.
     */
    private boolean serviceCollectionExistInMainApplicationClassPackage() {
        Class<EnableServiceCollection> type = EnableServiceCollection.class;
        if (mainApplicationClass.isAnnotationPresent(type)) {
            return true;
        }
        ClassPathScanningCandidateComponentProvider provider
                = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                AnnotationMetadata metadata = beanDefinition.getMetadata();
                return metadata.isIndependent()
                        && !Objects.equals(metadata.getSuperClassName(), Enum.class.getName());
            }
        };
        provider.addIncludeFilter(new AnnotationTypeFilter(type));
        String packageName = mainApplicationClass.getPackage().getName();
        while (packageName.contains(".")) {
            packageName = packageName.substring(0, packageName.lastIndexOf("."));
            if (!provider.findCandidateComponents(packageName).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        if (!context.getBeanFactory().containsBean(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR)
                && serviceCollectionExistInMainApplicationClassPackage()) {
            setCustomBeanNameGeneratorForSpringApplicationContext(context);
        }
    }

    /**
     * Set up a custom bean name generator {@code BeanNameGenerator} for
     * {@link ConfigurableApplicationContext}, mainly to adapt to the current
     * framework components.
     *
     * <p>Users can call this method at different stages of Spring's startup
     * lifecycle to set custom {@code BeanNameGenerator}, but if you need to
     * define it in advance, be sure to do so before the {@link #contextPrepared}
     * method, otherwise the existence of {@code @EnableServiceCollection} will
     * be scanned based on the package path where the main class is launched.
     *
     * @param context the configurable spring context.
     */
    public static void setCustomBeanNameGeneratorForSpringApplicationContext(ConfigurableApplicationContext context) {
        Class<? extends ConfigurableApplicationContext> contextClass = context.getClass();
        Method method = ReflectionUtils
                .findMethod(contextClass, "setBeanNameGenerator", BeanNameGenerator.class);
        if (method != null) {
            ServiceContextBeanNameGenerator beanNameGenerator = new ServiceContextBeanNameGenerator();
            context.getBeanFactory().registerSingleton(ServiceDefinitionUtils.INTERNAL_BEAN_NAME_GENERATOR_BEAN_NAME,
                    beanNameGenerator);
            ReflectionUtils.invokeMethod(method, context, beanNameGenerator);
        }
    }
}
