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

package top.osjf.spring.autoconfigure.cron;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.repository.CronListenerRepository;
import top.osjf.cron.core.util.CollectionUtils;

import java.util.List;

/**
 * Automatically register {@link CronListener} to the listener of the
 * corresponding scheduled task component.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.1
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CronListenerRegistrant implements InitializingBean {

    private final CronListenerRepository cronListenerRepository;

    private List<CronListener> cronListeners;

    public CronListenerRegistrant(CronListenerRepository cronListenerRepository) {
        this.cronListenerRepository = cronListenerRepository;
    }

    @Autowired(required = false)
    public void setCronListeners(List<CronListener> cronListeners) {
        this.cronListeners = cronListeners;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (CollectionUtils.isNotEmpty(cronListeners)) {
            cronListenerRepository.addCronListeners(cronListeners);
        }
    }
}
