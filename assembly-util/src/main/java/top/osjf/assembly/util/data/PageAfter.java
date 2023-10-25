//
// Copyright: Hangzhou Boku Network Co., Ltd
// ......
// Copyright Maintenance Date: 2022-2023
// ......
// Direct author: Zhang Pengfei
// ......
// Author email: 929160069@qq.com
// ......
// Please indicate the source for reprinting use
//

package top.osjf.assembly.util.data;

import java.util.List;
import java.util.function.Consumer;

/**
 * The post pagination operation allows for special processing of pagination results.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 */
public interface PageAfter<T> {

    PageResult<T> after(Consumer<List<T>> consumer);
}
