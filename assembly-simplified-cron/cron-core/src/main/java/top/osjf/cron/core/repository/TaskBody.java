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


package top.osjf.cron.core.repository;

import top.osjf.cron.core.lang.Wrapper;

/**
 * This interface is a tagging interface specifically designed to identify
 * that its implementation class is an executor of a scheduled task.
 *
 * <p>Subclass can implement the interface as needed and provide detailed
 * metadata and related behaviors of the request body in the class.
 *
 * <p>This interface implements {@link Wrapper} and can always perform supervisor
 * conversion. In subsequent usage scenarios, conversion operations can be performed
 * based on the current usage type, making it more convenient and accurate to handle.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface TaskBody extends Wrapper {
}
