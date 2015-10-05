package org.terasoluna.gfw.boot.autoconfigure;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.terasoluna.gfw.common.date.ClassicDateFactory;
import org.terasoluna.gfw.common.date.DefaultClassicDateFactory;
import org.terasoluna.gfw.common.exception.ExceptionCodeResolver;
import org.terasoluna.gfw.common.exception.ExceptionLogger;
import org.terasoluna.gfw.common.exception.ResultMessagesLoggingInterceptor;
import org.terasoluna.gfw.common.exception.SimpleMappingExceptionCodeResolver;
import org.terasoluna.gfw.web.codelist.CodeListInterceptor;
import org.terasoluna.gfw.web.exception.ExceptionLoggingFilter;
import org.terasoluna.gfw.web.exception.HandlerExceptionResolverLoggingInterceptor;
import org.terasoluna.gfw.web.exception.SystemExceptionResolver;
import org.terasoluna.gfw.web.logging.TraceLoggingInterceptor;

@Configuration
@ConditionalOnClass({ExceptionLogger.class})
@EnableConfigurationProperties(TerasolunaProperties.class)
@AutoConfigureAfter(JodaTimeDateFactoryAutoConfiguration.class)
public class TerasolunaAutoConfiguration {
    @Autowired
    TerasolunaProperties terasolunaProperties;

    @Bean
    public ExceptionCodeResolver exceptionCodeResolver() {
        SimpleMappingExceptionCodeResolver exceptionCodeResolver = new SimpleMappingExceptionCodeResolver();
        exceptionCodeResolver.setExceptionMappings(terasolunaProperties.getException().getCodeMappings());
        exceptionCodeResolver.setDefaultExceptionCode(terasolunaProperties.getException().getDefaultExceptionCode());
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
    public ResultMessagesLoggingInterceptor resultMessagesLoggingInterceptor(ExceptionLogger exceptionLogger) {
        ResultMessagesLoggingInterceptor resultMessagesLoggingInterceptor = new ResultMessagesLoggingInterceptor();
        resultMessagesLoggingInterceptor.setExceptionLogger(exceptionLogger);
        return resultMessagesLoggingInterceptor;
    }

    @Bean
    @ConditionalOnProperty(prefix = TerasolunaProperties.TERASOLUNA_PREFIX, name = "exception.logging-advisor-enabled", matchIfMissing = true)
    public Advisor resultMessagesLoggingAdvisor(ExceptionLogger exceptionLogger) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@within(org.springframework.stereotype.Service)");
        ResultMessagesLoggingInterceptor interceptor = new ResultMessagesLoggingInterceptor();
        interceptor.setExceptionLogger(exceptionLogger);
        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }

    @Bean
    @ConditionalOnMissingBean
    public ClassicDateFactory dateFactory() {
        return new DefaultClassicDateFactory();
    }

    @Configuration
    @ConditionalOnProperty(prefix = TerasolunaProperties.TERASOLUNA_PREFIX, name = "mvc.enabled", matchIfMissing = true)
    public static class WebMvcConfig extends WebMvcConfigurerAdapter {
        @Autowired
        TerasolunaProperties terasolunaProperties;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new TraceLoggingInterceptor());
            registry.addInterceptor(codeListInterceptor());
        }

        @Bean
        public CodeListInterceptor codeListInterceptor() {
            CodeListInterceptor codeListInterceptor = new CodeListInterceptor();
            codeListInterceptor.setCodeListIdPattern(terasolunaProperties.getMvc().getCodeListIdPattern());
            return codeListInterceptor;
        }

        @Bean
        @ConditionalOnMissingBean
        public SystemExceptionResolver systemExceptionResolver() {
            TerasolunaProperties.Mvc mvc = terasolunaProperties.getMvc();
            SystemExceptionResolver exceptionResolver = new SystemExceptionResolver();
            exceptionResolver.setOrder(3);
            exceptionResolver.setExceptionMappings(mvc.getExceptionMappings());
            exceptionResolver.setStatusCodes(mvc.getStatusCodes());
            exceptionResolver.setDefaultErrorView(mvc.getDefaultErrorView());
            exceptionResolver.setStatusCodes(mvc.getStatusCodes());
            return exceptionResolver;
        }

        @Bean
        public HandlerExceptionResolverLoggingInterceptor handlerExceptionResolverLoggingInterceptor(ExceptionLogger exceptionLogger) {
            HandlerExceptionResolverLoggingInterceptor interceptor = new HandlerExceptionResolverLoggingInterceptor();
            interceptor.setExceptionLogger(exceptionLogger);
            return interceptor;
        }

        @Bean
        public Advisor resultMessagesLoggingAdvisor(ExceptionLogger exceptionLogger) {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("execution(* org.springframework.web.servlet.HandlerExceptionResolver.resolveException(..))");
            ResultMessagesLoggingInterceptor interceptor = new ResultMessagesLoggingInterceptor();
            interceptor.setExceptionLogger(exceptionLogger);
            return new DefaultPointcutAdvisor(pointcut, interceptor);
        }
    }


}
