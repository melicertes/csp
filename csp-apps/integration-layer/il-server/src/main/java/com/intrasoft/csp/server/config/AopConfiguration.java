package com.intrasoft.csp.server.config;

import com.intrasoft.csp.server.utils.CSPTraceLogInterceptor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@Aspect
public class AopConfiguration {

    @Pointcut(
            "execution(* com.intrasoft.csp.server.processors..*(..))"
    )
    public void traceLogger() { }

    @Bean
    public CSPTraceLogInterceptor traceLoggerInterceptor() {
        return new CSPTraceLogInterceptor(true);
    }

    @Bean
    public Advisor traceLoggerAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("com.intrasoft.csp.server.config.AopConfiguration.traceLogger()");
        return new DefaultPointcutAdvisor(pointcut, traceLoggerInterceptor());
    }

}