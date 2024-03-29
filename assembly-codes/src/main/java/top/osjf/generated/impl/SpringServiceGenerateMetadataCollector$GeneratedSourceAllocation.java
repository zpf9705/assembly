package top.osjf.generated.impl;

import top.osjf.assembly.util.lang.MapUtils;
import top.osjf.generated.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The iterator logic of {@link SpringServiceGenerateMetadataCollector} is implemented in the
 * public {@link AbstractGeneratedSourceAllocation}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 */
public class SpringServiceGenerateMetadataCollector$GeneratedSourceAllocation extends AbstractGeneratedSourceAllocation {
    private final SpringServiceGroup group;

    public SpringServiceGenerateMetadataCollector$GeneratedSourceAllocation(SpringServiceGroup group) {
        this.group = group;
        setEntries(Arrays.stream(group.group())
                .map((Function<SpringServiceSource, GroupSourceEntry>) springServiceSource ->
                        new SpringServiceGenerateMetadataCollector$GeneratedSourceAllocation$GroupSourceEntry(
                                springServiceSource, this))
                .collect(Collectors.toList()));
    }

    @Override
    public String getUnifiedPackageName() {
        return group.packageName();
    }

    @Override
    public ClassKind getUnifiedClassKind() {
        return ClassKind.CLASS;
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
     * of {@link SpringServiceGenerateMetadataCollector}.
     */
    public static class SpringServiceGenerateMetadataCollector$GeneratedSourceAllocation$GroupSourceEntry implements
            GeneratedSourceAllocation.GroupSourceEntry {

        private static final String SPRING_SERVICE_NAME = "org.springframework.stereotype.Service";

        private final SpringServiceSource source;

        private final SpringServiceGenerateMetadataCollector$GeneratedSourceAllocation allocation;

        public SpringServiceGenerateMetadataCollector$GeneratedSourceAllocation$GroupSourceEntry(
                SpringServiceSource source,
                SpringServiceGenerateMetadataCollector$GeneratedSourceAllocation allocation) {
            this.source = source;
            this.allocation = allocation;
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
            return null;
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
            Map<String, String> annotationNameSources =
                    GeneratedUtils.convertAnnotationNameSources(source.annotationSources());
            if (MapUtils.isEmpty(annotationNameSources)) {
                annotationNameSources = new HashMap<>();
                Map<String, String> parentAnnotationSources = allocation.getUnifiedAnnotationSources();
                if (MapUtils.isNotEmpty(parentAnnotationSources)) {
                    annotationNameSources.putAll(parentAnnotationSources);
                }
            }
            annotationNameSources.putIfAbsent(SPRING_SERVICE_NAME, source.value());
            return annotationNameSources;
        }
    }
}
