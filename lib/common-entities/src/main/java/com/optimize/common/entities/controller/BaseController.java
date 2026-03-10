package com.optimize.common.entities.controller;

import com.optimize.common.entities.config.CustomMessageSource;
import com.optimize.common.entities.entity.BaseEntity;
import com.optimize.common.entities.service.GenericService;
import com.optimize.common.entities.util.Response;
import com.optimize.common.entities.util.ResponseUtil;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.Serializable;


@Getter
public abstract class BaseController<E extends BaseEntity<?>, I extends Serializable> extends CommonAdviceController {
    protected final GenericService<E, I> service;

    public BaseController(CustomMessageSource messageSource, GenericService<E, I> service) {
        super(messageSource);
        this.service = service;
    }

    public Response success(Object data, String message) {
        return ResponseUtil
                .successResponse(data,  messageSource.getMessage(message));
    }

    public Response success(Object data, String message, HttpStatus status) {
        return ResponseUtil
                .successResponse(data,  messageSource.getMessage(message), status);
    }

    public Response success(Object data, String message, HttpStatus status, String service) {
        return ResponseUtil
                .successResponse(data,  messageSource.getMessage(message), status, service);
    }

    public ResponseEntity<Response> getAll(Pageable pageable) {
        return new ResponseEntity<>(success(service.getAll(pageable), "operations.get-all.success"), HttpStatus.OK);
    }

    public ResponseEntity<Response> getAll() {
        return new ResponseEntity<>(success(service.getAll(), "operations.get-all.success"), HttpStatus.OK);
    }

    public ResponseEntity<Response> getOne(@PathVariable I id) {
        return new ResponseEntity<>(success(service.getById(id), "operations.get-one.success"), HttpStatus.OK);
    }

    public ResponseEntity<Response> deleteSoft(@PathVariable I id) {
        return new ResponseEntity<>(success(service.deleteSoft(id), "operations.delete.success"), HttpStatus.OK);
    }
    //TODO: phoneNumber validation annotation | field dependant not null annotation | photo base64 format validation annotation
}
