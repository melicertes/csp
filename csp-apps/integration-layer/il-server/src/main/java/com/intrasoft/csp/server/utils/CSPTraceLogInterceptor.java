package com.intrasoft.csp.server.utils;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.AbstractMonitoringInterceptor;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSPTraceLogInterceptor extends AbstractMonitoringInterceptor {


    public CSPTraceLogInterceptor(boolean dynamicLogger) {
        setUseDynamicLogger(dynamicLogger);
    }

    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log log) throws Throwable {
        String name = createInvocationTraceName(invocation);
        long start = System.currentTimeMillis();

        if (log.isTraceEnabled()) {
            log.trace("START: Method -> " + name + " ( " + Stream.of(invocation.getArguments()).map(a -> a.getClass().getSimpleName()).collect(Collectors.joining(","))+ " ) ");
        }

        try {
            return invocation.proceed();
        } finally {
            if (log.isTraceEnabled()) {
                long end = System.currentTimeMillis();
                long time = end - start;
                log.trace("END  : Method -> " + name + " (" + time + " ms)");
            }
        }
    }
}
