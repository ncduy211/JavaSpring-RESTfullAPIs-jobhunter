package vn.duync.jobhunter.util.error;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import vn.duync.jobhunter.domain.RestResponse;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class })
    public ResponseEntity<RestResponse<Object>> handleException(Exception exception) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        restResponse.setError("Exception");
        restResponse.setMessage(exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> validationError(
            MethodArgumentNotValidException methodArgumentNotValidException) {
        BindingResult result = methodArgumentNotValidException.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();

        RestResponse<Object> restResponse = new RestResponse<>();

        restResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        restResponse.setError(methodArgumentNotValidException.getBody().getDetail());

        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }
        restResponse.setMessage(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }

    @ExceptionHandler(value = IdInvalidException.class)
    public ResponseEntity<RestResponse<Object>> handleEmailHasExistedError(Exception exception) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        restResponse.setError("Bad Request");
        restResponse.setMessage(exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleNotFoundId(Exception exception) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        restResponse.setError("Not Found");
        restResponse.setMessage(exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleNotFound(Exception exception) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        restResponse.setError("404 Not found. URL không tồn tại");
        restResponse.setMessage(exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }

}
