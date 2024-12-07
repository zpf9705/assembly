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

package top.osjf.sdk.http.annotation.processor;

import top.osjf.sdk.http.annotation.HttpSdkEnumCultivate;
import top.osjf.sdk.http.annotation.resolver.HttpSdkEnumResolver;
import top.osjf.sdk.http.annotation.resolver.Resolver;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@SupportedAnnotationTypes({GenericAnnotationProcessor.HTTP_SDK_ENUM})
public class GenericAnnotationProcessor extends AbstractProcessor {

    /**
     * Support for {@link HttpSdkEnumCultivate}.
     */
    static final String HTTP_SDK_ENUM = "top.osjf.sdk.http.annotation.HttpSdkEnumCultivate";

    private ProcessingEnvironment processingEnv;
    private Map<String, Resolver> resolverMap;
    private Resolver.ResolverMetadata initResolverMetadata;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.initResolverMetadata = Resolver.toMetadata(processingEnv);
        initResolver();
    }

    private void initResolver() {
        this.resolverMap = new LinkedHashMap<>();
        this.resolverMap.put(HTTP_SDK_ENUM, new HttpSdkEnumResolver());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }
        if (this.initResolverMetadata == null) {
            return false;
        }
        for (TypeElement annotation : annotations) {
            Resolver resolver = resolverMap.get(annotation.getQualifiedName().toString());
            if (resolver != null && resolver.test(initResolverMetadata)) {
                resolver.resolve(initResolverMetadata.createProcess(roundEnv));
            }
        }
        return true;
    }
}
