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

package top.osjf.sdk.http.annotation.processor;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.annotation.DefaultCultivateHttpSdkEnum;
import top.osjf.sdk.http.annotation.HttpSdkEnumCultivate;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.Set;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class HttpSdkEnumResolver implements Resolver {

    private static final String METHOD_NAME = "matchSdkEnum";

    private static final String STATIC_VAR_TYPE = DefaultCultivateHttpSdkEnum.class.getSimpleName();

    private static final String IMPORT_STATIC_VAR_TYPE = DefaultCultivateHttpSdkEnum.class.getName();

    private static final String STATIC_VAR_NAME = "DEFAULT_HTTP_SDK_ENUM";

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

//        if (!isRequestAssignableFrom(element)) return;

        TreeMaker treeMaker = resolverMetadata.getTreeMaker();

        Names names = resolverMetadata.getNames();

        JCTree.JCClassDecl classDecl = resolverMetadata.getJavacTrees().getTree(element);

        // 创建静态私有变量
//        JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PRIVATE | Flags.STATIC);
//        Name fieldName = names.fromString("staticPrivateField");
//        JCTree.JCExpression fieldType = treeMaker.TypeIdent(TypeTag.INT);
//        JCTree.JCVariableDecl fieldDecl = treeMaker.VarDef(
//                modifiers,
//                fieldName,
//                fieldType,
//                null
//        );
        // 创建静态私有对象
//        JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PRIVATE | Flags.STATIC);
//        Name fieldName = names.fromString("staticPrivateObject");
//        JCTree.JCExpression fieldType = treeMaker.Ident(names.fromString("InvoiceApply"));
//        JCTree.JCNewClass newClass = treeMaker.NewClass(null, List.nil(),
//                treeMaker.Ident(names.fromString("InvoiceApply")), List.nil(), null);
//        JCTree.JCVariableDecl fieldDecl = treeMaker.VarDef(
//                modifiers,
//                fieldName,
//                fieldType,
//                newClass
//        );


        JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PRIVATE | Flags.STATIC | Flags.FINAL);

        Name fieldName = names.fromString(STATIC_VAR_NAME);

        JCTree.JCExpression fieldType = treeMaker.Ident(names.fromString("DefaultCultivateHttpSdkEnum"));

        HttpSdkEnumCultivate enumCultivate = element.getAnnotation(HttpSdkEnumCultivate.class);

        String name = enumCultivate.name();

        name = StringUtils.isNotBlank(enumCultivate.name()) ? name : element.getQualifiedName().toString();


        JCTree.JCNewClass newClass = treeMaker.NewClass(
                null,
                List.nil(),
                treeMaker.Ident(names.fromString("DefaultCultivateHttpSdkEnum")),
                List.of(treeMaker.Literal(enumCultivate.url()),
                        treeMaker.Literal(enumCultivate.version()),
                        treeMaker.Literal(enumCultivate.protocol().name()),
                        treeMaker.Literal(enumCultivate.method().name()),
                        treeMaker.Literal(name)),
                null
        );

        JCTree.JCVariableDecl fieldDecl = treeMaker.VarDef(
                modifiers,
                fieldName,
                fieldType,
                newClass
        );


//
//        JCTree.JCImport anImport
//                = treeMaker.Import(memberAccess(IMPORT_STATIC_VAR_TYPE, treeMaker, names), false);
//
//        List<JCTree> jcTrees = List.of(anImport, fieldDecl);

        classDecl.defs = classDecl.defs.prepend(fieldDecl);


//        JCTree.JCVariableDecl variableDecl = treeMaker.VarDef(
//                treeMaker.Modifiers(Flags.PRIVATE),
//                names.fromString("invoiceApply0"),
//                treeMaker.Ident(names.fromString("InvoiceApply")),
//                treeMaker.NewClass(null,
//                        List.nil(),
//                        treeMaker.Ident(names.fromString("InvoiceApply")),
//                        List.nil(),
//                        null));
//
//        classDecl.defs = classDecl.defs.prepend(variableDecl);

        //Change the variable of method `matchSdkEnum` to DEFAULT_HTTP_SDK_ENUM.
//        classDecl.defs.stream()
//                .filter(jct -> jct instanceof JCTree.JCMethodDecl
//                        && METHOD_NAME.equals(((JCTree.JCMethodDecl) jct).name.toString()))
//                .findFirst()
//                .ifPresent(jcTree -> {
//                    JCTree.JCBlock body = ((JCTree.JCMethodDecl) jcTree).getBody();
//                    body.getStatements()
//                            .stream()
//                            .filter(jcs -> jcs instanceof JCTree.JCReturn)
//                            .findFirst()
//                            .ifPresent(jcStatement -> {
//                                JCTree.JCReturn jcReturn = (JCTree.JCReturn) jcStatement;
//                                jcReturn.expr = treeMaker.Literal(STATIC_VAR_NAME);
//                            });
//                });
    }

    private JCTree.JCExpression memberAccess(String components, TreeMaker treeMaker, Names names) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(names.fromString(componentArray[0]));

        for (int i = 1; i < componentArray.length; ++i) {
            expr = treeMaker.Select(expr, names.fromString(componentArray[i]));
        }
        return expr;
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
