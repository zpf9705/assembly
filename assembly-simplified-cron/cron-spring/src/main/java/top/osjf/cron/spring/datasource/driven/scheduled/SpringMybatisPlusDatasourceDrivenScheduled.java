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


package top.osjf.cron.spring.datasource.driven.scheduled;

import com.baomidou.mybatisplus.extension.service.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.env.Environment;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.core.util.StringUtils;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;
import top.osjf.cron.datasource.driven.scheduled.mp.DatabaseTaskElement;
import top.osjf.cron.datasource.driven.scheduled.mp.MybatisPlusDatasourceDrivenScheduled;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring-integrated implementation of datasource-driven scheduled task manager with MyBatis-Plus persistence.
 *
 * <p>This class serves as the central management component for Spring-based scheduled tasks
 * driven by database configurations. It integrates with Spring's application context,
 * environment, and expression language (SpEL) to provide dynamic task execution capabilities.</p>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Spring Integration: Implements multiple Spring lifecycle interfaces
 *       (EnvironmentAware, InitializingBean, etc.)</li>
 *   <li>Dynamic Task Resolution: Uses SpEL expressions in task names for method invocation</li>
 *   <li>Profile-Based Activation: Filters tasks using Spring's active profiles</li>
 *   <li>Expression Caching: Optimizes SpEL expression parsing with concurrent cache</li>
 *   <li>Environment Overrides: Supports configurable logger and main task ID via properties</li>
 * </ul>
 *
 * <h2>Lifecycle Management:</h2>
 * <ol>
 *   <li>Initialization: Triggered by Spring's {@code afterPropertiesSet()} method</li>
 *   <li>Startup: Auto-starts on {@code ContextRefreshedEvent}</li>
 *   <li>Shutdown: Properly stops tasks via {@code DisposableBean} interface</li>
 * </ol>
 *
 * <h2>SpEL Integration:</h2>
 * <p>Supports dynamic method invocation through task names containing SpEL expressions.
 * Example formats:</p>
 * <pre>
 *   1. No-argument method: "@beanName.methodName()"
 *   2. Parameterized method: "@beanName.methodName('arg1', 123)"
 *   3. Chained calls: "@beanName.service.getData().process()"
 * </pre>
 *
 * <h2>Configuration Properties:</h2>
 * <dl>
 *   <dt>spring.schedule.cron.datasource.driven.logger-name</dt>
 *   <dd>Customize logger name (overrides default class-based logger)</dd>
 *
 *   <dt>spring.schedule.cron.datasource.driven.main-task-id</dt>
 *   <dd>Override management task ID</dd>
 * </dl>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class SpringMybatisPlusDatasourceDrivenScheduled
        extends MybatisPlusDatasourceDrivenScheduled implements EnvironmentAware, InitializingBean,
        ApplicationContextAware, ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    private final SpelExpressionParser expressionParser = new SpelExpressionParser();
    private final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();

    private Environment environment;
    private final List<String> activeProfiles = new ArrayList<>();
    private ApplicationContext applicationContext;

    private Logger logger;

    /**
     * Constructs a new {@code SpringMybatisPlusDatasourceDrivenScheduled} with {@code CronTaskRepository}
     * as its task Manager and {@code IService<DatabaseTaskElement>} as its task information storage.
     *
     * @param cronTaskRepository the Task management resource explorer.
     * @param taskElementService the task information storage service.
     */
    public SpringMybatisPlusDatasourceDrivenScheduled(CronTaskRepository cronTaskRepository,
                                                      IService<DatabaseTaskElement> taskElementService) {
        super(cronTaskRepository, taskElementService);
    }

    @Override
    public void setEnvironment(Environment environment) {
        activeProfiles.addAll(Arrays.asList(environment.getActiveProfiles()));
        this.environment = environment;
    }

    @Override
    public void afterPropertiesSet() {
        evaluationContext.setBeanResolver(new BeanFactoryResolver(applicationContext.getAutowireCapableBeanFactory()));
        init();
        //init setting logger
        String loggerName = environment
                .getProperty("spring.schedule.cron.datasource.driven.logger-name", "");
        if (!StringUtils.isBlank(loggerName)) {
            logger = LoggerFactory.getLogger(loggerName);
        }
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        start();
    }

    /**
     * {@inheritDoc}
     * <p>Use Spring's environment {@link Environment#getActiveProfiles()} for task
     * registration environment matching.
     */
    @Override
    protected boolean profilesMatch(String profiles) {
        return StringUtils.isBlank(profiles)
                || Arrays.stream(profiles.replace("，", ",").split(","))
                .anyMatch(s -> activeProfiles.contains(s.trim()));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Method formulation {@link TaskElement#getTaskName()} is the el expression for Spring,
     * precise to a certain method of the bean, provided by Spring expansion for parsing.
     * <pre>
     *         // Example 1: Call the Bean's non parametric method
     *         // Expression format: "@beanName.methodName()"
     *         Expression noArgExpr = parser.parseExpression("@exampleService.sayHello()");
     *         String result1 = noArgExpr.getValue(evaluationContext, String.class);
     *         System.out.println("No parameter method call result: " + result1);
     *
     *         // Example 2: Call Bean's parameterized method
     *         // Expression format: "@beanName.methodName(arg1, arg2)"
     *         Expression withArgExpr = parser.parseExpression("@exampleService.concatenate('Hello', 'SpEL')");
     *         String result2 = withArgExpr.getValue(evaluationContext, String.class);
     *         System.out.println("Result of method call with parameters: " + result2);
     *
     *         // Example 3: Call Bean's method and use the return value
     *         Expression valueExpr = parser.parseExpression("@exampleService.getGreeting().toUpperCase()");
     *         String result3 = valueExpr.getValue(evaluationContext, String.class);
     *         System.out.println("Chain call result: " + result3);
     * </pre>
     */
    @NotNull
    @Override
    protected Runnable resolveTaskRunnable(TaskElement element) {
        String taskName = element.getTaskName();
        return () -> expressionCache.computeIfAbsent(taskName, s -> expressionParser.parseExpression(taskName)).getValue();
    }

    @Override
    public void destroy() {
        stop();
    }

    @Override
    protected Logger getLogger() {
        return logger != null ? logger : super.getLogger();
    }

    @Override
    protected String getManagerTaskId() {
        return environment.getProperty("spring.schedule.cron.datasource.driven.main-task-id", super.getManagerTaskId());
    }
}
