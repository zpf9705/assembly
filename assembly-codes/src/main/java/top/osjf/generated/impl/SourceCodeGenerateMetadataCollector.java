package top.osjf.generated.impl;

import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.generated.*;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The integration logic of annotation processor {@link GeneratedSourceGroup} for
 * {@link GeneratedSourceGroupProcessor}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see GeneratedSourceGroup
 * @see GeneratedSourceGroupProcessor
 * @since 1.1.3
 */
class SourceCodeGenerateMetadataCollector extends AbstractImplMetadataCollector<GeneratedSourceGroup> {

    public SourceCodeGenerateMetadataCollector(GeneratedSourceGroup annotation, RoundEnvironment roundEnvironment,
                                               Element element,
                                               Filer filer,
                                               Logger logger) {
        super(annotation, roundEnvironment, element, filer, logger);
    }

    @Override
    @NotNull
    public GeneratedSourceAllocation getGeneratedSourceAllocation() {
        return new SourceCodeGenerateMetadataCollector$GeneratedSourceAllocation(getAnnotation());
    }

    /**
     * The iterator logic of {@link SourceCodeGenerateMetadataCollector} is implemented in the
     * public {@link AbstractGeneratedSourceAllocation}.
     */
    static class SourceCodeGenerateMetadataCollector$GeneratedSourceAllocation extends AbstractGeneratedSourceAllocation {

        private final GeneratedSourceGroup group;

        private final List<GroupSourceEntry> entries;

        public SourceCodeGenerateMetadataCollector$GeneratedSourceAllocation(GeneratedSourceGroup group) {
            this.group = group;
            entries = Arrays.stream(group.group())
                    .map((Function<GeneratedSource, GroupSourceEntry>)
                            SourceCodeGenerateMetadataCollector$GeneratedSourceAllocation$GroupSourceEntry::new)
                    .collect(Collectors.toList());
        }

        @Override
        public String getPackageName() {
            return group.packageName();
        }

        @Override
        public ClassKind getClassKind() {
            return group.classKind();
        }

        @Override
        public String getExtendClassName() {
            return group.extendClassName();
        }

        @Override
        public String[] getExtendGenericsClassNames() {
            return group.extendGenericsClassNames();
        }

        @Override
        public Map<String, String[]> getInterfaceClassSources() {
            return GeneratedUtils.convertInterfaceClassNameSources(group.interfaceClassSources());
        }

        @Override
        public Map<String, String> getAnnotationSources() {
            return GeneratedUtils.convertAnnotationNameSources(group.annotationSources());
        }

        @Override
        public int getSize() {
            return entries.size();
        }

        @Override
        @NotNull
        public List<GroupSourceEntry> getEntries() {
            return entries;
        }
    }

    /**
     * The implementation class for interface {@link GeneratedSourceAllocation.GroupSourceEntry}
     * of {@link SourceCodeGenerateMetadataCollector}.
     */
    static class SourceCodeGenerateMetadataCollector$GeneratedSourceAllocation$GroupSourceEntry implements
            GeneratedSourceAllocation.GroupSourceEntry{

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
