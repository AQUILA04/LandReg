package com.optimize.common.entities.logger;

import org.apache.commons.io.IOUtils;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CustomHttpRequestWrapper extends HttpServletRequestWrapper {
    byte[] byteArray;

    public CustomHttpRequestWrapper(HttpServletRequest request) {
        super(request);
        try {
            byteArray = IOUtils.toByteArray(request.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException("Issue while reading request stream");
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CustomDelegatingInputStream(new ByteArrayInputStream(byteArray));
    }
}
