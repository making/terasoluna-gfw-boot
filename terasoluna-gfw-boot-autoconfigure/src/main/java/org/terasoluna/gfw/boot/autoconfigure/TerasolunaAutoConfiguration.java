package org.terasoluna.gfw.boot.autoconfigure;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.terasoluna.gfw.common.date.ClassicDateFactory;
import org.terasoluna.gfw.common.date.DefaultClassicDateFactory;
import org.terasoluna.gfw.common.exception.ExceptionCodeResolver;
import org.terasoluna.gfw.common.exception.ExceptionLogger;
import org.terasoluna.gfw.common.exception.ResultMessagesLoggingInterceptor;
import org.terasoluna.gfw.common.exception.SimpleMappingExceptionCodeResolver;
import org.terasoluna.gfw.web.exception.ExceptionLoggingFilter;

@Configuration
@ConditionalOnClass({ExceptionLogger.class})
@EnableConfigurationProperties(TerasolunaProperties.class)
public class TerasolunaAutoConfiguration {
    @Autowired
    TerasolunaProperties terasolunaProperties;

    @Bean
    public ExceptionCodeResolver exceptionCodeResolver() {
        SimpleMappingExceptionCodeResolver exceptionCodeResolver = new SimpleMappingExceptionCodeResolver();
        exceptionCodeResolver.setExceptionMappings(terasolunaProperties.getException().getCodeMappings());
        return exceptionCodeResolver;
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionLogger exceptionLogger(ExceptionCodeResolver exceptionCodeResolver) {
        ExceptionLogger exceptionLogger = new ExceptionLogger();
        exceptionLogger.setExceptionCodeResolver(exceptionCodeResolver);
        return exceptionLogger;
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionLoggingFilter exceptionLoggingFilter(ExceptionLogger exceptionLogger) {
        ExceptionLoggingFilter exceptionLoggingFilter = new ExceptionLoggingFilter();
        exceptionLoggingFilter.setExceptionLogger(exceptionLogger);
        return exceptionLoggingFilter;
    }

    @Bean
    @ConditionalOnMissingBean
    ResultMessagesLoggingInterceptor resultMessagesLoggingInterceptor(ExceptionLogger exceptionLogger) {
        ResultMessagesLoggingInterceptor resultMessagesLoggingInterceptor = new ResultMessagesLoggingInterceptor();
        resultMessagesLoggingInterceptor.setExceptionLogger(exceptionLogger);
        return resultMessagesLoggingInterceptor;
    }

    @Bean
    @ConditionalOnProperty(prefix = TerasolunaProperties.TERASOLUNA_PREFIX, name = "exception.logging-advisor-enabled", matchIfMissing = true)
    Advisor resultMessagesLoggingAdvisor(ExceptionLogger exceptionLogger) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@within(org.springframework.stereotype.Service)");
        ResultMessagesLoggingInterceptor interceptor = new ResultMessagesLoggingInterceptor();
        interceptor.setExceptionLogger(exceptionLogger);
        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }

    @Bean
    @ConditionalOnMissingBean
    ClassicDateFactory dateFactory() {
        return new DefaultClassicDateFactory();
    }
}
