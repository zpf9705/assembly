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


package top.osjf.cron.spring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import top.osjf.cron.core.repository.CronTaskInfo;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.core.util.CollectionUtils;
import top.osjf.cron.core.util.ReflectUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class CronTaskInfoReadableWebMvcHandlerController
        implements InitializingBean, Supplier<ResponseEntity<List<CronTaskInfoView>>> {

    public static final String REQUEST_MAPPING_PATH = "/cronTask/list";

    private final CronTaskRepository cronTaskRepository;

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    private final RequestMappingInfo requestMappingInfo
            = RequestMappingInfo.paths(REQUEST_MAPPING_PATH).methods(RequestMethod.GET).build();

    private final Method method = ReflectUtils.getMethod(this.getClass(), "get");

    public CronTaskInfoReadableWebMvcHandlerController(CronTaskRepository cronTaskRepository,
                                                       RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.cronTaskRepository = cronTaskRepository;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @Override
    public void afterPropertiesSet() {
        requestMappingHandlerMapping.registerMapping(requestMappingInfo, this, method);
    }

    @Override
    @ResponseBody
    public ResponseEntity<List<CronTaskInfoView>> get() {
        List<CronTaskInfo> cronTaskInfos = cronTaskRepository.getAllCronTaskInfo();
        if (CollectionUtils.isEmpty(cronTaskInfos)) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(cronTaskInfos.stream().map(CronTaskInfoView::new).collect(Collectors.toList()));
    }
}
