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


package top.osjf.cron.spring.annotation;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.datasource.driven.scheduled.mp.DatabaseTaskElement;
import top.osjf.cron.spring.datasource.driven.scheduled.SpringMybatisPlusDatasourceDrivenScheduled;

/**
 * {@link Configuration Configuration} for {@link SpringMybatisPlusDatasourceDrivenScheduled}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@Configuration(proxyBeanMethods = false)
@MapperScan("top.osjf.cron.datasource.driven.scheduled.mapper")
@ComponentScan("top.osjf.cron.datasource.driven.scheduled.manager")
public class DatabaseDrivenScheduledConfiguration {

    @Bean
    public SpringMybatisPlusDatasourceDrivenScheduled springMybatisPlusDatasourceDrivenScheduled
            (CronTaskRepository cronTaskRepository, IService<DatabaseTaskElement> taskElementService) {
        return new SpringMybatisPlusDatasourceDrivenScheduled(cronTaskRepository, taskElementService);
    }
}
