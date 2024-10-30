package com.huyenhm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.huyenhm.common.ResponseBean;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ResponseBean> handleResourceNotFound(ResourceNotFoundException ex) {
		ResponseBean response = new ResponseBean(404, ex.getMessage(), null);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ResponseBean> handleUnauthorized(UnauthorizedException ex) {
		ResponseBean response = new ResponseBean(401, ex.getMessage(), null);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	@ExceptionHandler(InvalidInputException.class)
	public ResponseEntity<ResponseBean> handleInvalidInput(InvalidInputException ex) {
		ResponseBean response = new ResponseBean(401, ex.getMessage(), null);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseBean> handleGenericException(Exception ex) {
		ResponseBean response = new ResponseBean(500, "An error occurred: " + ex.getMessage(), null);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

}
