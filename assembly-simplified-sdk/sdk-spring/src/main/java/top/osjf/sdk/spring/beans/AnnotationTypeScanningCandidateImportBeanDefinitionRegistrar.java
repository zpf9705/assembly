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

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import top.osjf.sdk.commons.annotation.NotNull;
import top.osjf.sdk.spring.annotation.EnableSdkProxyRegister;
import top.osjf.sdk.spring.annotation.Sdk;
import top.osjf.sdk.spring.proxy.ConcentrateProxySupport;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * Manually register objects into the support classes of the Spring container.
 *
 * <p>With annotation usage as the core, the general process is to query and annotate
 * an interface class for a certain annotation, construct a {@link BeanDefinition} based
 * on the information, and inject it into the spring container.
 *
 * <p>This process can be enabled using an annotation switch.
 *
 * <p>For example, when the springboot program starts, if it detects that the container bean is
 * wearing {@link EnableSdkProxyRegister} annotations,
 * it will automatically search for the wearing annotation {@link Sdk}
 * interface in this class and create an implementation class (refer to {@link ConcentrateProxySupport}).
 *
 * <p>The implementation process depends on the bean's registrar interface {@link ImportBeanDefinitionRegistrar}.
 *
 * <p>The lookup of annotated interfaces depends on the interface {@link ResourceLoader} and
 * {@link ClassPathScanningCandidateComponentProvider}.
 *
 * <p>This registration scheme does not participate in calling services such as {@link Autowired}
 * before spring bean registration, and is executed as a registration scheme here.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AnnotationTypeScanningCandidateImportBeanDefinitionRegistrar
        extends ScanningCandidateImportBeanDefinitionRegistrar<AnnotatedBeanDefinition> {

    @NotNull
    @Override
    protected ClassPathScanningCandidateComponentProvider getScanningCandidateProvider() {
        return new AnnotationTypeClassPathScanningCandidateComponentProvider(
                getEnvironment(), getResourceLoader(), getFilterAnnotationType());
    }

    @Override
    protected boolean isAvailableMarkedBeanDefinition(BeanDefinition markedBeanDefinition) {
        if (markedBeanDefinition instanceof AnnotatedBeanDefinition) {
            return isAvailableMarkedBeanDefinitionMetadata(((AnnotatedBeanDefinition) markedBeanDefinition)
                    .getMetadata());
        }
        return false;
    }

    @Override
    protected BeanDefinitionHolder createBeanDefinitionHolder(AnnotatedBeanDefinition markedBeanDefinition) {
        AnnotationMetadata markedAnnotationMetadata = markedBeanDefinition.getMetadata();
        AnnotationAttributes markedAnnotationAttributes = AnnotationAttributes
                .fromMap(markedAnnotationMetadata.getAnnotationAttributes(getFilterAnnotationType().getCanonicalName()));
        return createBeanDefinitionHolder(markedAnnotationAttributes, markedAnnotationMetadata);
    }

    /**
     * Return the search resource class to filter the type of annotation.
     *
     * <p>For clarity of meaning, the name ‘getFilterAnnotationType’
     * was changed to version 2.2.5.
     *
     * @return the search resource class to filter the type of annotation,
     * must not be {@literal null}.
     */
    @NotNull
    protected abstract Class<? extends Annotation> getFilterAnnotationType();

    /**
     * Return the custom valid judgment result of the associated
     * metadata {@link AnnotationMetadata} of {@link AnnotatedBeanDefinition}.
     *
     * @param metadata the {@link AnnotationMetadata} provided by the
     *                 {@link AnnotatedBeanDefinition}.
     * @return the custom valid judgment result of the associated
     * metadata {@link AnnotationMetadata} of {@link AnnotatedBeanDefinition}.
     */
    protected boolean isAvailableMarkedBeanDefinitionMetadata(AnnotationMetadata metadata) {
        return true;
    }

    /**
     * Returns holder for a BeanDefinition with name and aliases.
     * <p>Update for 2.2.5
     *
     * @param markedAnnotationAttributes Mark the attribute map of the annotation.
     * @param markedAnnotationMetadata   Annotate the metadata of the annotation class.
     * @return Holder for a BeanDefinition with name and aliases.
     */
    protected abstract BeanDefinitionHolder createBeanDefinitionHolder(AnnotationAttributes markedAnnotationAttributes,
                                                                       AnnotationMetadata markedAnnotationMetadata);

    /*** To find a specialized rewrite query for {@link AnnotatedBeanDefinition} using annotations.*/
    public static class AnnotationTypeClassPathScanningCandidateComponentProvider
            extends ClassPathScanningCandidateComponentProvider {

        public AnnotationTypeClassPathScanningCandidateComponentProvider(Environment environment,
                                                                         ResourceLoader resourceLoader,
                                                                         Class<? extends Annotation> filterAnnotationType) {
            super(false, environment);
            setResourceLoader(resourceLoader);
            addIncludeFilter(new AnnotationTypeFilter(filterAnnotationType));
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            AnnotationMetadata metadata = beanDefinition.getMetadata();
            return metadata.isIndependent()
                    &&
                    !metadata.isAnnotation()
                    &&
                    !Objects.equals(metadata.getSuperClassName(), Enum.class.getName());
        }
    }
}
