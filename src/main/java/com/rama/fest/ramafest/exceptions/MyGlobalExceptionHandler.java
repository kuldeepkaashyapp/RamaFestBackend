package com.rama.fest.ramafest.exceptions;

import com.rama.fest.ramafest.dto.response.APIResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice

public class MyGlobalExceptionHandler {



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> myConstraintViolationException(MethodArgumentNotValidException e){
        Map<String, String> map = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach((error)->{
            String fieldName=((FieldError)error).getField();
            String message = error.getDefaultMessage();
            map.put(fieldName,message);        });
        return ResponseEntity.badRequest().body(map);

    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> myConstraintViolationException(ConstraintViolationException e){
        Map<String, String> response = new HashMap<>();

      e.getConstraintViolations().forEach((error)->{

        String fieldName=error.getPropertyPath().toString();

      // sending error from the Code
        String message = error.getMessage();
        response.put(fieldName,message);
      });
        return new ResponseEntity<Map<String,String>>(response, HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException e){
            String message = e.getMessage();
            APIResponse response = APIResponse.builder().message(message).status(false).build();
            return  new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }


    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIException(APIException e){
        String  message = e.getMessage();

        APIResponse response = APIResponse.builder().message(message).status(false).build();
        return  new ResponseEntity<APIResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> myIllegalArgumentException(
            IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }




}
