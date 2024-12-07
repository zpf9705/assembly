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

package top.osjf.sdk.http.annotation.intelligence;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.util.ReflectUtil;

import java.util.ArrayList;

/**
 * A list class that simplifies and modifies the abstract
 * tree of AST code.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class IntelligenceJCTreeList extends ArrayList<JCTree> {

    private final JCTree jcTree;

    private List<JCTree> defs;

    public IntelligenceJCTreeList(JCTree jcTree) {
        this.jcTree = jcTree;
        defs = ReflectUtil.getFieldValue(jcTree, "defs");
    }

    public JCTree getJcTree() {
        return jcTree;
    }

    @Override
    public boolean add(@NotNull JCTree jcTree) {
        defs = defs.prepend(jcTree);
        return true;
    }
}
