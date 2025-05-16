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
 * Cron Task Information Readable Web MVC Handler Controller.
 *
 * <p>This controller is responsible for handling HTTP GET requests related to Cron
 * task information.It retrieves Cron task information from CronTaskRepository and
 * converts it into a list of {@code CronTaskInfoView} objects for return.
 *
 * <p>By implementing the InitializingBean interface, this class automatically invokes
 * the {@link #afterPropertiesSet} method after the Spring container is initialized.
 * In this method, it registers the current controller and its get method to the
 * {@code RequestMappingHandlerMapping}, so that the Spring MVC framework can recognize
 * and handle the corresponding HTTP requests.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class CronTaskInfoReadableWebMvcHandlerController
        implements InitializingBean, Supplier<ResponseEntity<List<CronTaskInfoView>>> {
    /**
     * Retrieve the web mapping path for all registered task information.
     */
    public static final String REQUEST_MAPPING_PATH_OF_GET_CRON_TASK_LIST = "/cronTask/list";
    /**
     * Query all registered task information for {@link RequestMappingInfo} objects.
     */
    private final RequestMappingInfo requestMappingInfoOfGetCronTaskList
            = RequestMappingInfo.paths(REQUEST_MAPPING_PATH_OF_GET_CRON_TASK_LIST).methods(RequestMethod.GET).build();
    /**
     * View processing method for querying all registered task information.
     */
    private final Method getCronTaskListHandlerMethod = ReflectUtils.getMethod(this.getClass(), "get");

    /**
     * The repository used to access and manipulate the storage of Cron task information.
     */
    private final CronTaskRepository cronTaskRepository;

    /**
     * The Spring MVC component used to register and handle HTTP request mappings.
     */
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    /**
     * Constructor initializes {@code CronTaskInfoReadableWebMvcHandlerController}, setting the
     * {@code CronTaskRepository} and {@code RequestMappingHandlerMapping}.
     *
     * @param cronTaskRepository           the cron task repository instance.
     * @param requestMappingHandlerMapping the web mvc request mapping handler instance.
     */
    public CronTaskInfoReadableWebMvcHandlerController(CronTaskRepository cronTaskRepository,
                                                       RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.cronTaskRepository = cronTaskRepository;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    /**
     * This method adds the path mapping for registering open web access to Spring MVC support.
     */
    @Override
    public void afterPropertiesSet() {
        requestMappingHandlerMapping
                .registerMapping(requestMappingInfoOfGetCronTaskList, this, getCronTaskListHandlerMethod);
    }

    /**
     * Get the list of Cron task information.
     *
     * <p>Handles HTTP GET requests, retrieves the list of Cron task information from the
     * {@code CronTaskRepository}, converts it into a list of {@code CronTaskInfoView} objects, and
     * returns it to the client.
     *
     * @return A response entity containing the list of Cron task information views
     * @see top.osjf.cron.spring.auth.WebRequestAuthenticationInterceptor
     */
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
