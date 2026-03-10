package com.optimize.common.entities.logger;

import org.apache.commons.io.output.TeeOutputStream;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class CustomHttpResponseWrapper extends HttpServletResponseWrapper {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(baos);

    public CustomHttpResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new CustomDelegatingServletOutputStream(new TeeOutputStream(super.getOutputStream(), printStream));
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new TeeOutputStream(super.getOutputStream(), printStream));
    }

    public ByteArrayOutputStream getBaos() {
        return baos;
    }
}
