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

import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import top.osjf.sdk.http.HttpProtocol;
import top.osjf.sdk.http.HttpRequestMethod;
import top.osjf.sdk.http.util.IOUtils;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@SupportedAnnotationTypes({"top.osjf.sdk.http.annotation.processor.HttpSdkEnumCodeGeneration"})
public class HttpSdkEnumCodeGenerationAnnotationProcessor extends AbstractProcessor {

    private ProcessingEnvironment processingEnv;
    private Messager messager;
    private JavacTrees javacTrees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnv = processingEnv;
        messager = processingEnv.getMessager();
        this.javacTrees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(HttpSdkEnumCodeGeneration.class);
        for (Element element : elements) {

            JCTree tree = javacTrees.getTree(element);

            System.out.println(element + " => JCTree toString : " + tree);

            tree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {

                    System.out.println(element + " => JCTree.JCClassDecl toString : " + jcClassDecl);

                    //collection JCTree.JCMethodDecl
                    List<JCTree.JCMethodDecl> jcMethodDecls = new ArrayList<>();
                    for (JCTree jcTree : jcClassDecl.defs) {
                        if (Objects.equals(Tree.Kind.METHOD, jcTree.getKind())) {
                            jcMethodDecls.add((JCTree.JCMethodDecl) jcTree);
                        }
                    }

                    if (!jcMethodDecls.isEmpty()) {

                        for (JCTree.JCMethodDecl jcMethodDecl : jcMethodDecls) {

                        }
                    }
                }
            });
        }
        return true;
    }

    private void processInternal(TypeElement element, HttpSdkEnumCodeGeneration codeGeneration) throws IOException {
        String qualifiedName = element.getQualifiedName().toString();
        System.out.println("qualifiedName:\n" + qualifiedName);

        String pkg = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
        System.out.println("pkg:\n" + pkg);

        CharSequence relativeName = element.getSimpleName() + JavaFileObject.Kind.SOURCE.extension;
        System.out.println("relativeName:\n" + relativeName);

        Filer filer = processingEnv.getFiler();
        String content;
        FileObject resource = filer.getResource(StandardLocation.SOURCE_PATH, pkg, relativeName);
        try (InputStream is = resource.openInputStream()) {
            content = new String(IOUtils.readAllBytes(is));
        }

        if (resource.delete()) {
            JavaFileObject fileObject = filer.createSourceFile(qualifiedName);
            try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
                writer.write(wrapperCodeContent(content, codeGeneration));
            }
        }

    }

    private String wrapperCodeContent(String codeContent, HttpSdkEnumCodeGeneration codeGeneration) {
        int firstBracketIndex = codeContent.indexOf("{");
        codeContent = new StringBuffer(codeContent)
                .insert(firstBracketIndex + 1, "\n\n\n" + getStaticHttpSdkEnumInstanceCode(codeGeneration) + "\n\n\n")
                .toString();
        return replaceStatic(codeContent);
    }

    private String getStaticHttpSdkEnumInstanceCode(HttpSdkEnumCodeGeneration codeGeneration) {
        HttpRequestMethod requestMethod = codeGeneration.method();
        HttpProtocol protocol = codeGeneration.protocol();
        boolean formatArgs = codeGeneration.formatArgs();
        String url = codeGeneration.url();
        String version = codeGeneration.version();
        String name = codeGeneration.name();
        return String.format("private static final HttpSdkEnum DEFAULT_HTTP_SDK = new HttpSdkEnum() {\n" +
                        "        @Override\n" +
                        "        public HttpRequestMethod getRequestMethod() {\n" +
                        "            return %s;\n" +
                        "        }\n" +
                        "        @Override\n" +
                        "        public HttpProtocol getProtocol() {\n" +
                        "            return %s;\n" +
                        "        }\n" +
                        "        @Override\n" +
                        "        public String getUrl(String host) {\n" +
                        "            return %s;\n" +
                        "        }\n" +
                        "        @Override\n" +
                        "        public String name() {\n" +
                        "            return %s;\n" +
                        "        }\n" +
                        "    };",
                getEnumCodeString(requestMethod),
                getHttpProtocolCodeString(protocol, formatArgs),
                getUrlCodeString(formatArgs, url, protocol, version),
                getNameCodeString(name));
    }

    private String replaceStatic(String codeContent) {
        return codeContent.replace("public HttpSdkEnum matchSdkEnum() {\n" +
                "        return null;\n" +
                "    }", "public HttpSdkEnum matchSdkEnum() {\n" +
                "        return DEFAULT_HTTP_SDK;\n" +
                "    }");
    }

    private String getUrlCodeString(boolean formatArgs, String url, HttpProtocol protocol, String version) {
        if (!formatArgs) {
            return "String.format(\"" + url + "\",host)";
        }
        return "String.format(\"" + url + "\"," + getEnumCodeString(protocol) + ",host,\"" + version + "\")";
    }

    @SuppressWarnings("rawtypes")
    private String getHttpProtocolCodeString(Enum e, boolean formatArgs) {
        return formatArgs ? "null" : e.getClass().getSimpleName() + "." + e.name();
    }

    @SuppressWarnings("rawtypes")
    private String getEnumCodeString(Enum e) {
        return e.getClass().getSimpleName() + "." + e.name();
    }

    private String getNameCodeString(String name) {
        return "\"" + name + "\"";
    }
}
