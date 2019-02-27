/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package it.fvaleri.repgen.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import it.fvaleri.repgen.Application.Config;

@Component
public class RequestInterceptor extends WebMvcConfigurerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(RequestInterceptor.class);

    @Autowired
    private Config config;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptorAdapter() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                    throws Exception {
                String requestURI = request.getRequestURI();
                LOG.trace("Request to {}", requestURI);
                MDC.put("in-memory-comp: ", Boolean.toString(config.isInMemory()));
                return super.preHandle(request, response, handler);
            }
        }).addPathPatterns("/**");
    }
}