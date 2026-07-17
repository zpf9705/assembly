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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.env.Environment;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.common.CompositeStringExpression;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.BeanReference;
import org.springframework.expression.spel.ast.MethodReference;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import top.osjf.commons.lang.NotNull;
import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.ReflectionUtils;
import top.osjf.commons.util.StringUtils;
import top.osjf.cron.core.repository.CronMethodRunnable;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.datasource.driven.scheduled.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static top.osjf.cron.spring.datasource.driven.scheduled.ScheduledDrivenPropertyKey.KEY_LOGGER_NAME;
import static top.osjf.cron.spring.datasource.driven.scheduled.ScheduledDrivenPropertyKey.KEY_MONITOR_CHECK_INTERNAL;

/**
 * {@code SpringDatasourceDrivenScheduled} Extension {@link AbstractDatasourceDrivenScheduled},
 * Spring integrates to implement a data-driven scheduling task manager, where task retrieval
 * relies on external data source operation interfaces {@link DatasourceTaskElementsOperation}.
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
 *   <dt>spring.schedule.cron.datasource.driven.main-task-unique-id</dt>
 *   <dd>Override management task unique ID</dd>
 * </dl>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class SpringDatasourceDrivenScheduled
        extends DefaultDatasourceDrivenScheduled
        implements EnvironmentAware, InitializingBean, ApplicationContextAware,
        ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    private final SpelExpressionParser expressionParser = new SpelExpressionParser();
    private final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();

    private Environment environment;
    private final List<String> activeProfiles = new ArrayList<>();
    private ApplicationContext applicationContext;

    private Logger customLogger;

    /**
     * Constructs a new {@code SpringDatasourceDrivenScheduled} with {@code CronTaskRepository}
     * as its task Manager and {@code DatasourceTaskElementsOperation} as its task information access.
     *
     * @param cronTaskRepository              the Task management resource explorer.
     * @param datasourceTaskElementsOperation the Task data source information retrieval operation interface.
     */
    public SpringDatasourceDrivenScheduled(CronTaskRepository cronTaskRepository,
                                           DatasourceTaskElementsOperation datasourceTaskElementsOperation) {
        super(cronTaskRepository, datasourceTaskElementsOperation);
    }

    @Override
    public void setEnvironment(Environment environment) {
        activeProfiles.addAll(Arrays.asList(environment.getActiveProfiles()));
        this.environment = environment;
    }

    @Override
    @Autowired(required = false)
    public void setResolvedRunnablePostProcessors(List<ResolvedRunnablePostProcessor> resolvedRunnablePostProcessors) {
        super.setResolvedRunnablePostProcessors(resolvedRunnablePostProcessors);
    }

    @Override
    @Autowired(required = false)
    public void setConfigLoader(DataSourceConfigLoader configLoader) {
        super.setConfigLoader(configLoader);
    }

    @Override
    public void afterPropertiesSet() {
        evaluationContext.setBeanResolver(new BeanFactoryResolver(applicationContext.getAutowireCapableBeanFactory()));
        initLogger();
        init();
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
    protected boolean profilesMatch(@NotNull String profiles) {
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
     *
     * @return {@inheritDoc}
     */

    @Nullable
    @Override
    protected Runnable resolveTaskRunnable(@NotNull TaskElement taskElement) {
        String taskName = taskElement.getTaskName();
        Expression expression;
        try {
            expression = expressionParser.parseExpression(taskName);
        }
        catch (ParseException ex) {
            recordState(taskElement, Status.PAUSED,
                    taskName + " does not conform to Spring EL expression rules");
            return null;
        }
        CronMethodRunnable methodRunnable = resolveExpressionToMethodRunnable(expression);
        if (methodRunnable == null) {
            return ()-> expression.getValue(evaluationContext);
        }
        return methodRunnable;
    }

    @Override
    public void destroy() {
        stop();
    }

    @Override
    @NotNull
    protected Logger getLogger() {
        return customLogger != null ? customLogger : super.getLogger();
    }

    private void initLogger() {
        String customLoggerName = environment.getProperty(KEY_LOGGER_NAME);
        if (StringUtils.isNotBlank(customLoggerName)) {
            customLogger = LoggerFactory.getLogger(customLoggerName);
        }
    }

    @Override
    protected long getTaskMonitorCheckInternal() {
        return environment.getProperty(KEY_MONITOR_CHECK_INTERNAL, long.class, super.getTaskMonitorCheckInternal());
    }

    /**
     * Resolve SpEL expression to {@code CronMethodRunnable} for cron task execution.
     * Only supports @beanName.methodName() format expressions.
     * @param expression Raw SpEL expression
     * @return {@code CronMethodRunnable} if parse success, {@literal null} otherwise.
     * @since 3.0.2
     */
    @Nullable
    private CronMethodRunnable resolveExpressionToMethodRunnable(Expression expression) {

        SpelExpression spelExpression = null;

        // Direct cast for standard SpEL expression
        if (expression instanceof SpelExpression) {
            spelExpression = (SpelExpression) expression;
        }
        // Handle template composite expression (mixed text with #{@bean.xxx()})
        // Traverse sub-expressions and pick the first SpEL logic segment
        else if (expression instanceof CompositeStringExpression) {
            for (Expression member : ((CompositeStringExpression) expression).getExpressions()) {
                if (member instanceof SpelExpression) {
                    spelExpression = (SpelExpression) member;
                    break;
                }
            }
        }

        if (spelExpression == null) {
            return null;
        }

        // Get AST root node of expression, scan for bean & method reference recursively
        SpelNode node = spelExpression.getAST();
        Reference reference = new Reference();
        doScanReference(reference, node);

        // Missing bean reference or method call node, invalid @bean.method() syntax
        if (reference.beanReference == null || reference.methodReference == null) {
            return null;
        }

        // Reflect private field "beanName" inside BeanReference to fetch target bean name
        Field beanNameField = ReflectionUtils
                .findField(BeanReference.class, "beanName", String.class);
        if (beanNameField == null) {
            return null;
        }
        ReflectionUtils.makeAccessible(beanNameField);
        String targetBeanName = (String) ReflectionUtils.getField(beanNameField, reference.beanReference);

        // Check if target bean exists in Spring ApplicationContext
        if (StringUtils.isBlank(targetBeanName) || !applicationContext.containsBean(targetBeanName)) {
            return null;
        }
        Object target = applicationContext.getBean(targetBeanName);

        // Get raw target class to eliminate AOP proxy wrapper
        Class<?> targetClass = AopUtils.getTargetClass(target);
        // Locate target method by method name from MethodReference
        Method method = ReflectionUtils
                .findMethod(targetClass, reference.methodReference.getName());
        // Target method not matched, return null
        if (method == null) {
            return null;
        }

        return new CronMethodRunnable(target, method) {
            /**
             * The actual method execution is not handed over to the incoming objects and
             * methods, but to the parsed expression for execution.
             */
            @Override
            public void run() {
                expression.getValue(evaluationContext);
            }
        };
    }

    /**
     * Recursively traverse SpEL AST nodes to locate BeanReference and MethodReference.
     * @param reference Container to store scanned bean reference and method reference
     * @param node Current traversed AST node
     */
    private void doScanReference(Reference reference, SpelNode node) {
        if (node instanceof BeanReference) {
            reference.beanReference = (BeanReference) node;
        }
        else if (node instanceof MethodReference) {
            reference.methodReference = (MethodReference) node;
        }
        if (reference.isComplete()) {
            return;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            doScanReference(reference, node.getChild(i));
            if (reference.isComplete()) {
                return;
            }
        }
    }

    /**
     * DTO to hold AST scan results: BeanReference and MethodReference nodes
     */
    static class Reference {
        /** SpEL bean reference node parsed from {@code @beanName} */
        BeanReference beanReference;
        /** SpEL method invocation node parsed from {@code .method()} */
        MethodReference methodReference;

        /**
         * Check whether both bean reference and method reference are successfully located
         * @return true if both references exist, false otherwise
         */
        public boolean isComplete() {
            return beanReference != null && methodReference != null;
        }
    }
}
