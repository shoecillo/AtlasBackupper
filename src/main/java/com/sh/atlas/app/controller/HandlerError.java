package com.sh.atlas.app.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sh.atlas.app.exceptions.BackupExistsException;



@ControllerAdvice
public class HandlerError extends ResponseEntityExceptionHandler {
	
	
	private static String STATUS = "status";
	private static String MESSAGE =  "message";
	private static Logger LOGGER = LoggerFactory.getLogger(HandlerError.class);
	
	@ExceptionHandler(Exception.class)
	private ResponseEntity<Object> handlerException(Exception ex){
		
		Map<String, Object> body = new LinkedHashMap<>();
		
		body.put(MESSAGE, ex.getMessage());
		body.put(STATUS, HttpStatus.BAD_REQUEST.value());
		LOGGER.error("Error: ",ex);
		return new ResponseEntity<Object>(body, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(BackupExistsException.class)
	private ResponseEntity<Object> handlerException(BackupExistsException ex){
		
		Map<String, Object> body = new LinkedHashMap<>();
		
		body.put(MESSAGE, ex.getMessage());
		body.put(STATUS, HttpStatus.BAD_REQUEST.value());
		LOGGER.warn(ex.getMessage());
		return new ResponseEntity<Object>(body, HttpStatus.BAD_REQUEST);
	}
	
	
}
