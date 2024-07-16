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

import org.springframework.core.type.ClassMetadata;
import top.osjf.sdk.commons.annotation.NotNull;
import top.osjf.sdk.commons.annotation.Nullable;

/**
 * Interface that defines abstract metadata of a specific class,
 * in a form that does not require that class to be loaded yet
 * (copy from {@link ClassMetadata}).
 *
 * <p>Aim to extend some property retrieval about configuration classes
 * based on {@link ClassMetadata}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface ConfiguredClassMetadata extends ClassMetadata {

    /**
     * Return the package name of the underlying class.
     *
     * @return the package name of the underlying class.
     */
    String getPackageName();

    /*** Build a simple {@link ConfiguredClassMetadata} using {@link ClassMetadata}.
     * @param classMetadata {@link ClassMetadata}.
     * @return {@link ConfiguredClassMetadata}.
     * */
    static ConfiguredClassMetadata of(ClassMetadata classMetadata) {
        return new DefaultConfiguredClassMetadata(classMetadata);
    }

    /*** Default impl for {@link ConfiguredClassMetadata}*/
    class DefaultConfiguredClassMetadata implements ConfiguredClassMetadata {

        private final ClassMetadata classMetadata;

        public DefaultConfiguredClassMetadata(ClassMetadata classMetadata) {
            this.classMetadata = classMetadata;
        }

        @Override
        public String getPackageName() {
            String className = getClassName();
            int theLastIndex = className.lastIndexOf(".");
            return getClassName().substring(0, theLastIndex);
        }

        @Override
        @NotNull
        public String getClassName() {
            return classMetadata.getClassName();
        }

        @Override
        public boolean isInterface() {
            return classMetadata.isInterface();
        }

        @Override
        public boolean isAnnotation() {
            return classMetadata.isAnnotation();
        }

        @Override
        public boolean isAbstract() {
            return classMetadata.isAbstract();
        }

        @Override
        public boolean isFinal() {
            return classMetadata.isFinal();
        }

        @Override
        public boolean isIndependent() {
            return classMetadata.isIndependent();
        }

        @Nullable
        @Override
        public String getEnclosingClassName() {
            return classMetadata.getEnclosingClassName();
        }

        @Nullable
        @Override
        public String getSuperClassName() {
            return classMetadata.getSuperClassName();
        }

        @Override
        @NotNull
        public String[] getInterfaceNames() {
            return classMetadata.getInterfaceNames();
        }

        @Override
        @NotNull
        public String[] getMemberClassNames() {
            return classMetadata.getMemberClassNames();
        }
    }
}
