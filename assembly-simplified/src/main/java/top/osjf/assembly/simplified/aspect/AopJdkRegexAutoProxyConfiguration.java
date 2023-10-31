package top.osjf.assembly.simplified.aspect;

import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.Objects;

/**
 * By obtaining the rules provided by the {@link EnableAopJdkRegexAutoProxy}
 * annotation, register a {@link Advisor} to intercept the corresponding
 * matching method.
 * <p>In the configuration of {@link #jdkRegexMatchAdvisor()}, you can learn
 * more about it.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.0.7
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class AopJdkRegexAutoProxyConfiguration implements ImportAware {

    private String patten;

    @Override
    public void setImportMetadata(@NotNull AnnotationMetadata metadata) {
        AnnotationAttributes attributes =
                AnnotationAttributes.fromMap(metadata
                        .getAnnotationAttributes(EnableAopJdkRegexAutoProxy.class.getName()));
        Objects.requireNonNull(attributes, EnableAopJdkRegexAutoProxy.class.getName()
                + " analysis failed.");
        patten = attributes.getString("patten");
    }

    @Bean
    public Advisor jdkRegexMatchAdvisor() {
        JdkRegexpMethodPointcut pointcut = new JdkRegexpMethodPointcut();
        pointcut.setPattern(patten);
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(new MethodInterceptorImpl());
        advisor.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return advisor;
    }
}
