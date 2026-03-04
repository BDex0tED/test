package com.manas.config;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class TraceIdFilter implements Filter {

    public static final String TRACE_ID_KEY = "traceId";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
    throws IOException, ServletException {
        String traceIdKey = UUID.randomUUID().toString();

        MDC.put(TRACE_ID_KEY, traceIdKey);

        try{
            filterChain.doFilter(servletRequest, servletResponse);
        } finally{
            MDC.remove(TRACE_ID_KEY);
        }
    }
}
