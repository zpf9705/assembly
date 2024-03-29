package top.osjf.generated.impl;

import top.osjf.generated.*;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The iterator logic of {@link SourceCodeGenerateMetadataCollector} is implemented in the
 * public {@link AbstractGeneratedSourceAllocation}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.3
 */
public class SourceCodeGenerateMetadataCollector$GeneratedSourceAllocation extends AbstractGeneratedSourceAllocation {
    private final GeneratedSourceGroup group;

    public SourceCodeGenerateMetadataCollector$GeneratedSourceAllocation(GeneratedSourceGroup group) {
        this.group = group;
        setEntries(Arrays.stream(group.group())
                .map((Function<GeneratedSource, GroupSourceEntry>)
                        SourceCodeGenerateMetadataCollector$GeneratedSourceAllocation$GroupSourceEntry::new)
                .collect(Collectors.toList()));
    }

    @Override
    public String getUnifiedPackageName() {
        return group.packageName();
    }

    @Override
    public ClassKind getUnifiedClassKind() {
        return group.classKind();
    }

    @Override
    public String getUnifiedExtendClassName() {
        return group.extendClassName();
    }

    @Override
    public String[] getUnifiedExtendGenericsClassNames() {
        return group.extendGenericsClassNames();
    }

    @Override
    public Map<String, String[]> getUnifiedInterfaceClassSources() {
        return GeneratedUtils.convertInterfaceClassNameSources(group.interfaceClassSources());
    }

    @Override
    public Map<String, String> getUnifiedAnnotationSources() {
        return GeneratedUtils.convertAnnotationNameSources(group.annotationSources());
    }

    /**
     * The implementation class for interface {@link GeneratedSourceAllocation.GroupSourceEntry}
     * of {@link SourceCodeGenerateMetadataCollector}.
     */
    public static class SourceCodeGenerateMetadataCollector$GeneratedSourceAllocation$GroupSourceEntry implements
            GeneratedSourceAllocation.GroupSourceEntry {

        private final GeneratedSource source;

        public SourceCodeGenerateMetadataCollector$GeneratedSourceAllocation$GroupSourceEntry(
                GeneratedSource source) {
            this.source = source;
        }

        @Override
        public String getSimpleName() {
            return source.simpleName();
        }

        @Override
        public String getPackageName() {
            return source.packageName();
        }

        @Override
        public ClassKind getClassKind() {
            return source.classKind();
        }

        @Override
        public String getExtendClassName() {
            return source.extendClassName();
        }

        @Override
        public String[] getExtendGenericsClassNames() {
            return source.extendGenericsClassNames();
        }

        @Override
        public Map<String, String[]> getInterfaceClassSources() {
            return GeneratedUtils.convertInterfaceClassNameSources(source.interfaceClassSources());
        }

        @Override
        public Map<String, String> getAnnotationSources() {
            return GeneratedUtils.convertAnnotationNameSources(source.annotationSources());
        }
    }
}
