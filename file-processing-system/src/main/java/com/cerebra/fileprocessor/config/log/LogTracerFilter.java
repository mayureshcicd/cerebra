package com.cerebra.fileprocessor.config.log;

import com.cerebra.fileprocessor.logger.LogTracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LogTracerFilter extends OncePerRequestFilter {
    private final LogTracer logTracer;
    private static final Logger LOGGER = LoggerFactory.getLogger(LogTracerFilter.class.getName());
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    public LogTracerFilter(LogTracer logTracer) {
        this.logTracer = logTracer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request == null) {
            LOGGER.error(ANSI_RED +" HttpServletRequest  is getting null   : "+ANSI_RESET);
        }

        logTracer.tracingID(request==null ?" ":request.getMethod());
        filterChain.doFilter(request, response);
    }
}