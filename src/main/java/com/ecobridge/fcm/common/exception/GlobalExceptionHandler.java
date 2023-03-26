package com.ecobridge.fcm.common.exception;

import com.ecobridge.fcm.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(basePackages = "com.ecobridge.fcm")
public class GlobalExceptionHandler {

    /*
    HttpRequestMethodNotSupportedException
    405 (SC_METHOD_NOT_ALLOWED)

    HttpMediaTypeNotSupportedException
    415 (SC_UNSUPPORTED_MEDIA_TYPE)

    HttpMediaTypeNotAcceptableException

  406 (SC_NOT_ACCEPTABLE)

      MissingPathVariableException

  500 (SC_INTERNAL_SERVER_ERROR)

      MissingServletRequestParameterException

  400 (SC_BAD_REQUEST)

      MissingServletRequestPartException

  400 (SC_BAD_REQUEST)

      ServletRequestBindingException

  400 (SC_BAD_REQUEST)

      ConversionNotSupportedException

  500 (SC_INTERNAL_SERVER_ERROR)

      TypeMismatchException

  400 (SC_BAD_REQUEST)

      HttpMessageNotReadableException

  400 (SC_BAD_REQUEST)

      HttpMessageNotWritableException

  500 (SC_INTERNAL_SERVER_ERROR)

      MethodArgumentNotValidException

  400 (SC_BAD_REQUEST)

      BindException

  400 (SC_BAD_REQUEST)

      NoHandlerFoundException

  404 (SC_NOT_FOUND)

      AsyncRequestTimeoutException

  503 (SC_SERVICE_UNAVAILABLE)

  */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception e, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", request.getRequestURI());
        log.error("Internal server error:", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                               .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                               .collect(Collectors.joining(", "));
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage, request.getRequestURI());
        log.error("@valid exception:", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
