package com.optimize.common.entities.logger;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Order(1)
@Slf4j(topic = "EventLog")
public class RequestResponseLoggers implements Filter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long time = System.currentTimeMillis();
        CustomHttpRequestWrapper httpRequestWrapper = new CustomHttpRequestWrapper((HttpServletRequest) servletRequest);
        Map<String, String> headers = Collections.list(httpRequestWrapper.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(h -> h, httpRequestWrapper::getHeader));

        CustomHttpResponseWrapper httpResponseWrapper = new CustomHttpResponseWrapper((HttpServletResponse) servletResponse);
        filterChain.doFilter(httpRequestWrapper, httpResponseWrapper);
        time = System.currentTimeMillis() - time;
        String requestBody = null;
        InputStream inputStream = httpRequestWrapper.getInputStream();
        if (inputStream != null) {
            byte[] requestBodyBytes = IOUtils.toByteArray(inputStream);
            requestBody = new String(requestBodyBytes).replace("\r\n", " ");
        }

        byte[] responseBodyBytes = null;
        ByteArrayOutputStream baos = httpResponseWrapper.getBaos();
        if (baos != null) {
            responseBodyBytes = baos.toByteArray();
        }
        String responseBody = responseBodyBytes != null ? new String(responseBodyBytes) : "";

        String api = ((HttpServletRequest) servletRequest).getRequestURI();


        log.debug("Method:  {} \n URL: {} \n Request Body: {} \n  Params: {} \n  Headers: {} \n  Status: {} \n  Response Body: {} \n  Time Taken: {} ms " ,
                httpRequestWrapper.getMethod(),
                httpRequestWrapper.getRequestURL() ,
                requestBody,
                httpRequestWrapper.getQueryString() ,
                new JSONObject(headers) ,
                httpResponseWrapper.getStatus() ,
                responseBody,
                time );
    }
}
