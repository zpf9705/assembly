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
 * @param <T> The type of result set item
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public interface PageAfter<T> {

    PageResult<T> after(Consumer<List<T>> consumer);
}
