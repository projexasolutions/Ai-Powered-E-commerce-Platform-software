package com.nextgen.store.config;

import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler({IllegalArgumentException.class,NoSuchElementException.class})
  ResponseEntity<Map<String,String>> badRequest(RuntimeException e){return ResponseEntity.badRequest().body(Map.of("message",safe(e.getMessage(),"Invalid request")));}
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException e){Map<String,Object> body=new LinkedHashMap<>();body.put("message","Validation failed");Map<String,String> fields=new LinkedHashMap<>();e.getBindingResult().getFieldErrors().forEach(x->fields.putIfAbsent(x.getField(),safe(x.getDefaultMessage(),"Invalid value")));body.put("fields",fields);return ResponseEntity.badRequest().body(body);}
  @ExceptionHandler({OptimisticLockException.class,OptimisticLockingFailureException.class})
  ResponseEntity<Map<String,String>> conflict(){return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","Stock changed while you were checking out. Please review your cart and try again."));}
  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<Map<String,String>> dataConflict(){return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","The request conflicts with existing data. Please retry."));}
  private static String safe(String value,String fallback){return value==null||value.isBlank()?fallback:value;}
}
