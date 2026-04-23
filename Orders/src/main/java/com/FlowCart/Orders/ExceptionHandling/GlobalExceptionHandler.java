package com.FlowCart.Orders.ExceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // This annotation indicates that this class will handle exceptions globally for all controllers in the application.
public class GlobalExceptionHandler {
    
    @ExceptionHandler(OrderNotFoundException.class) // This annotation indicates that this method will handle exceptions of type OrderNotFoundException.
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleOrderNotFoundException(OrderNotFoundException ex) {
        return ex.getMessage();
    } // This method handles OrderNotFoundException and returns a 404 Not Found status with the exception message as the response body.
}
