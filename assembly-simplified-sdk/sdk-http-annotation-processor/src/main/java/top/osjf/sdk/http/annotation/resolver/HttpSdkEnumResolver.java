/*
 * Copyright 2023-2024 the original author or authors.
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

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.annotation.HttpSdkEnumCultivate;
import top.osjf.sdk.http.annotation.intelligence.DefaultCultivateHttpSdkEnum;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.Set;

/**
 * The implementation class for processing {@code Resolver} related
 * services annotated with {@link HttpSdkEnumCultivate}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class HttpSdkEnumResolver implements Resolver {

    static final String METHOD_NAME = "matchSdkEnum";
    static final String DEFAULT_VAR_NAME = "DEFAULT_HTTP_SDK_ENUM";
    static final String INTELLIGENCE_SIMPLE_NAME = DefaultCultivateHttpSdkEnum.class.getSimpleName();
    static final String INTELLIGENCE_PACKAGE_NAME = DefaultCultivateHttpSdkEnum.class.getPackage().getName();

    @Override
    public void resolve(ResolverMetadata resolverMetadata) {

        RoundEnvironment roundEnv = resolverMetadata.getProcessRoundEnv();

        if (roundEnv == null) return;

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(HttpSdkEnumCultivate.class);

        for (Element element : elements) {

            resolveInternal((TypeElement) element, resolverMetadata);
        }
    }

    private void resolveInternal(TypeElement element, ResolverMetadata resolverMetadata) {

        TreeMaker treeMaker = resolverMetadata.getTreeMaker();

        Names names = resolverMetadata.getNames();

        JCTree.JCClassDecl classDecl = resolverMetadata.getJavacTrees().getTree(element);

        JCTree.JCVariableDecl staticVar = getStaticVar(treeMaker, names, element);

        classDecl.defs = classDecl.defs.prepend(staticVar);

        JCTree.JCCompilationUnit unitTree =
                (JCTree.JCCompilationUnit) resolverMetadata.getJavacTrees().getPath(element).getCompilationUnit();

        JCTree.JCImport jcImport = getImport(treeMaker, names);

        unitTree.defs = unitTree.defs.prepend(jcImport);

        updateMethodVar(classDecl, treeMaker, names);
    }

    private JCTree.JCVariableDecl getStaticVar(TreeMaker maker, Names names, TypeElement element) {

        JCTree.JCModifiers modifiers = maker.Modifiers(Flags.PRIVATE | Flags.STATIC | Flags.FINAL);

        Name fieldName = names.fromString(DEFAULT_VAR_NAME);

        JCTree.JCExpression fieldType = maker.Ident(names.fromString(INTELLIGENCE_SIMPLE_NAME));

        HttpSdkEnumCultivate cultivate = element.getAnnotation(HttpSdkEnumCultivate.class);

        String name = StringUtils.isNotBlank(cultivate.name()) ? cultivate.name() : element.getQualifiedName().toString();

        JCTree.JCNewClass newClass = maker.NewClass(
                null,
                List.nil(),
                maker.Ident(names.fromString(INTELLIGENCE_SIMPLE_NAME)),
                List.of(maker.Literal(cultivate.url()),
                        maker.Literal(cultivate.version()),
                        maker.Literal(cultivate.protocol().name()),
                        maker.Literal(cultivate.method().name()),
                        maker.Literal(name)),
                null
        );

        return maker.VarDef(modifiers, fieldName, fieldType, newClass);

    }

    private JCTree.JCImport getImport(TreeMaker maker, Names names) {
        JCTree.JCIdent packageJCIdent = maker.Ident(names.fromString(INTELLIGENCE_PACKAGE_NAME));
        Name className = names.fromString(INTELLIGENCE_SIMPLE_NAME);
        return maker.Import(maker.Select(packageJCIdent, className), false);
    }

    private void updateMethodVar(JCTree.JCClassDecl classDecl, TreeMaker treeMaker, Names names) {
        classDecl.defs.stream()
                .filter(jct -> jct instanceof JCTree.JCMethodDecl
                        && METHOD_NAME.equals(((JCTree.JCMethodDecl) jct).name.toString()))
                .findFirst()
                .ifPresent(jcTree -> {
                    JCTree.JCBlock body = ((JCTree.JCMethodDecl) jcTree).getBody();
                    body.getStatements()
                            .stream()
                            .filter(jcs -> jcs instanceof JCTree.JCReturn)
                            .findFirst()
                            .ifPresent(jcStatement -> {
                                JCTree.JCReturn jcReturn = (JCTree.JCReturn) jcStatement;
                                jcReturn.expr = treeMaker.Ident(names.fromString(DEFAULT_VAR_NAME));
                            });
                });
    }

    private boolean isRequestAssignableFrom(TypeElement element) {
        boolean isRequestAssignableFrom = false;

        for (TypeMirror typeMirror : element.getInterfaces()) {
            System.out.println(typeMirror.toString());
        }
        TypeMirror superclass = element.getSuperclass();
        System.out.println(superclass);
        return true;
    }
}
