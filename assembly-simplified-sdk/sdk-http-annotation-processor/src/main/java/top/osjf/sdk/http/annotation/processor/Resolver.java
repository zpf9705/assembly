package top.osjf.sdk.http.annotation.processor;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import top.osjf.sdk.core.support.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface Resolver {

    void resolve(ResolverMetadata resolverMetadata);

    interface ResolverMetadata {

        ProcessingEnvironment getProcessingEnvironment();

        JavacTrees getJavacTrees();

        TreeMaker getTreeMaker();

        Names getNames();

        ResolverMetadata createProcess(RoundEnvironment roundEnv);

        @Nullable
        RoundEnvironment getProcessRoundEnv();
    }


    class DefaultResolverMetadata implements ResolverMetadata {

        private final ProcessingEnvironment processingEnv;
        private final JavacTrees javacTrees;
        private final TreeMaker treeMaker;
        private final Names names;
        private RoundEnvironment roundEnv;

        public DefaultResolverMetadata(ProcessingEnvironment processingEnv,
                                       JavacTrees javacTrees,
                                       TreeMaker treeMaker,
                                       Names names) {
            this.processingEnv = processingEnv;
            this.javacTrees = javacTrees;
            this.treeMaker = treeMaker;
            this.names = names;
        }

        DefaultResolverMetadata setProcessRoundEnv(RoundEnvironment roundEnv) {
            this.roundEnv = roundEnv;
            return this;
        }

        @Override
        public ProcessingEnvironment getProcessingEnvironment() {
            return processingEnv;
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
        public ResolverMetadata createProcess(RoundEnvironment roundEnv) {
            return new DefaultResolverMetadata(processingEnv, javacTrees,
                    treeMaker, names).setProcessRoundEnv(roundEnv);
        }

        @Nullable
        @Override
        public RoundEnvironment getProcessRoundEnv() {
            return roundEnv;
        }
    }
}
