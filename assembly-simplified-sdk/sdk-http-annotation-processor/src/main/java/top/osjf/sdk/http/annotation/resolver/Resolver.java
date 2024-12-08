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

package top.osjf.sdk.http.annotation.resolver;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import top.osjf.sdk.core.support.Nullable;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.function.Predicate;

/**
 * A {@code Resolver} interface defining specifications for handling relevant
 * metadata information in an annotation processor.
 *
 * <p>This interface includes a method {@link #resolve} that accepts a
 * {@code ResolverMetadata} object for processing given metadata.
 * It also includes a static method {@link #toMetadata} that converts
 * an annotation processor-initialized {@code ProcessingEnvironment}
 * object into a {@code ResolverMetadata} object.
 *
 * <p>{@code ResolverMetadata} is an inner interface defining various
 * tools and methods obtained from the {@code ProcessingEnvironment}
 * for accessing and manipulating metadata during annotation processing.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface Resolver extends Predicate<Resolver.ResolverMetadata> {

    /**
     * Calculate whether the given initialization {@code ResolverMetadata}
     * meets the requirements.
     *
     * @param initResolverMetadata {@inheritDoc}.
     * @return If {@literal true}, it can proceed to the processing stage,
     * otherwise it does not meet the usage conditions.
     */
    @Override
    default boolean test(ResolverMetadata initResolverMetadata) {
        return true;
    }

    /**
     * Process a given {@link ResolverMetadata DefaultResolverMetadata} object.
     *
     * @param resolverMetadata {@link ResolverMetadata DefaultResolverMetadata} object.
     */
    void resolve(ResolverMetadata resolverMetadata);

    /**
     * Transform an annotation processor initialized {@link ProcessingEnvironment}
     * object into a metadata processing object {@code ResolverMetadata}.
     *
     * @param processingEnv annotation processor initialized {@link ProcessingEnvironment}.
     * @return metadata processing object {@code ResolverMetadata}.
     */
    static ResolverMetadata toMetadata(ProcessingEnvironment processingEnv) {
        return new DefaultResolverMetadata(processingEnv);
    }

    /**
     * Analyze the relevant processing metadata information interface
     * obtained from {@code ProcessingEnvironment}.
     */
    interface ResolverMetadata {

        /**
         * Returns the filer used to create new source, class, or auxiliary
         * files.
         *
         * @return the filer
         */
        Filer getFiler();

        /**
         * Returns the messager used to report errors, warnings, and other
         * notices.
         *
         * @return the messager.
         */
        Messager getMessager();

        /**
         * Returns an implementation of some utility methods for
         * operating on elements
         *
         * @return element utilities.
         */
        Elements getElements();

        /**
         * Returns an implementation of some utility methods for
         * operating on types.
         *
         * @return type utilities.
         */
        Types getTypes();

        /**
         * Returns a {@code JavacTrees} instance for accessing abstract
         * syntax trees.
         *
         * @return The {@code JavacTrees} object.
         */
        JavacTrees getJavacTrees();

        /**
         * Returns a {@code TreeMaker} instance for creating new AST nodes.
         *
         * @return The {@code TreeMaker}  object.
         */
        TreeMaker getTreeMaker();

        /**
         * Returns a {@code Names} instance for handling name-related operations.
         *
         * @return The {@code Names} object.
         */
        Names getNames();

        /**
         * Return the environment variables of the current processor.
         *
         * @return the environment variables of the current processor.
         */
        ProcessingEnvironment getProcessingEnvironment();

        /**
         * Creates a new {@code ResolverMetadata} object based on the given
         * the current processing {@code RoundEnvironment}
         *
         * @param roundEnv the current processing {@code RoundEnvironment}.
         * @return a new {@code ResolverMetadata} object.
         */
        ResolverMetadata createProcess(RoundEnvironment roundEnv);

        /**
         * Returns the RoundEnvironment of the current processing round.
         *
         * <p>Whether the {@link RoundEnvironment} obtained by this method
         * is {@literal null} depends on whether the current round's
         * {@code RoundEnvironment} is set through {@link #createProcess},
         * without calling the specific initialization configuration variable
         * returned by method {@link #createProcess}, and not the current round.
         *
         * @return the current processing {@code RoundEnvironment}.
         */
        @Nullable
        RoundEnvironment getProcessRoundEnv();
    }

    /**
     * Default impl for {@link ResolverMetadata}.
     */
    class DefaultResolverMetadata implements ResolverMetadata {

        private final Filer filer;
        private final Messager messager;
        private final Types types;
        private final Elements elements;
        private JavacTrees javacTrees;
        private TreeMaker treeMaker;
        private Names names;

        private final ProcessingEnvironment processingEnv;

        private RoundEnvironment roundEnv;

        DefaultResolverMetadata(ProcessingEnvironment processingEnv) {
            this.filer = processingEnv.getFiler();
            this.messager = processingEnv.getMessager();
            this.elements = processingEnv.getElementUtils();
            this.types = processingEnv.getTypeUtils();
            this.processingEnv = processingEnv;
            if (processingEnv instanceof JavacProcessingEnvironment) {
                Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
                this.javacTrees = JavacTrees.instance(context);
                this.treeMaker = TreeMaker.instance(context);
                this.names = Names.instance(context);
            }
        }

        DefaultResolverMetadata(Filer filer,
                                Messager messager,
                                Types types,
                                Elements elements,
                                JavacTrees javacTrees,
                                TreeMaker treeMaker,
                                Names names,
                                ProcessingEnvironment processingEnv) {
            this.filer = filer;
            this.messager = messager;
            this.types = types;
            this.elements = elements;
            this.javacTrees = javacTrees;
            this.treeMaker = treeMaker;
            this.names = names;
            this.processingEnv = processingEnv;
        }

        DefaultResolverMetadata setProcessRoundEnv(RoundEnvironment roundEnv) {
            this.roundEnv = roundEnv;
            return this;
        }

        @Override
        public Filer getFiler() {
            return filer;
        }

        @Override
        public Messager getMessager() {
            return messager;
        }

        @Override
        public Elements getElements() {
            return elements;
        }

        @Override
        public Types getTypes() {
            return types;
        }

        @Override
        public JavacTrees getJavacTrees() {
            return javacTrees;
        }

        @Override
        public TreeMaker getTreeMaker() {
            return treeMaker;
        }

        @Override
        public Names getNames() {
            return names;
        }

        @Override
        public ProcessingEnvironment getProcessingEnvironment() {
            return processingEnv;
        }

        @Override
        public ResolverMetadata createProcess(RoundEnvironment roundEnv) {
            return new DefaultResolverMetadata(filer, messager, types, elements,
                    javacTrees, treeMaker, names, processingEnv)
                    .setProcessRoundEnv(roundEnv);
        }

        @Nullable
        @Override
        public RoundEnvironment getProcessRoundEnv() {
            return roundEnv;
        }
    }
}
