package org.estore.e_store_order_service.advices;

import java.util.Arrays;

import org.estore.e_store_order_service.exceptions.*;
import org.estore.e_store_order_service.response.ApiResponse;
import org.estore.e_store_order_service.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    

     @ExceptionHandler(UnauthorizeException.class)
    public ResponseEntity<?> handleUnauthorizeNotFoundException(UnauthorizeException ex) {

        return buildResponseEntityWithApiResponse(
                ErrorResponse.builder()
                        .status(401)
                        .error(ex.getMessage())
                        .build());
    }

        @ExceptionHandler(FailureException.class)
    public ResponseEntity<?> handleServiceFailureException(FailureException ex) {

        return buildResponseEntityWithApiResponse(
                ErrorResponse.builder()
                        .status(408)
                        .error(ex.getMessage())
                        .suberrors(Arrays.asList("Some part of the System is down"))
                        .build());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<?> handleInvalidReqException(InvalidRequestException ex) {

        return buildResponseEntityWithApiResponse(
                ErrorResponse.builder()
                        .status(400)
                        .error(ex.getMessage())
                        .build());
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {

        return buildResponseEntityWithApiResponse(
                ErrorResponse.builder()
                        .status(404)
                        .error(ex.getMessage())
                        .build());
    }


    private ResponseEntity<ApiResponse<?>> buildResponseEntityWithApiResponse(ErrorResponse errorResponse) {
        ApiResponse<ErrorResponse> errApiResponse = new ApiResponse<>(errorResponse);
        errApiResponse.setStatus(errorResponse.getStatus());
        return new ResponseEntity<>(errApiResponse, HttpStatus.valueOf(errorResponse.getStatus()));
    }
}
