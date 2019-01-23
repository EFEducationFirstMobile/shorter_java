package com.ef.shorturl.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class UrlExceptionHandler extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler(ResponseStatusException.class)
	public final ResponseEntity<ErrorDetails> handleAllExceptions(ResponseStatusException ex, WebRequest request) {
	  ErrorDetails errorDetails = new ErrorDetails(ex.getReason());
	  return new ResponseEntity<>(errorDetails, ex.getStatus());
	}
}

class ErrorDetails {
	String error;
	
	public ErrorDetails(String error) {
		this.error = error;
	}
	
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}
}

  