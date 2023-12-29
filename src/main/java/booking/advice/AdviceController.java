package booking.advice;

import booking.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.util.HashMap;
import java.util.Map;
@ControllerAdvice
public class AdviceController {
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<String> loginFail(LoginException loginException) {
        return new ResponseEntity<>(loginException.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RegisterException.class)
    public ResponseEntity<String> registerFail(RegisterException registerException) {
        return new ResponseEntity<>(registerException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> notFound(NotFoundException notFoundException) {
        return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExistsException.class)
    public ResponseEntity<String> exists(ExistsException existsException){
        return new ResponseEntity<>(existsException.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OutOfDateException.class)
    public ResponseEntity<String> outOfDate(OutOfDateException outOfDateException){
        return new ResponseEntity<>(outOfDateException.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AddMenuException.class)
    public ResponseEntity<String> addMenu(AddMenuException addMenuException){
        return new ResponseEntity<>(addMenuException.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TimeInputException.class)
    public ResponseEntity<String> timeInput(TimeInputException timeInputException){
        return new ResponseEntity<>(timeInputException.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> accessDenied(AccessDeniedException accessDeniedException){
        return new ResponseEntity<>(accessDeniedException.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidException.class)
    public ResponseEntity<String> invalid(InvalidException invalidException){
        return new ResponseEntity<>(invalidException.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyException.class)
    public ResponseEntity<String> empty(EmptyException emptyException){
        return new ResponseEntity<>(emptyException.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> noValue(NoSuchElementException noSuchElementException){
        return new ResponseEntity<>(noSuchElementException.getMessage(),HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(OverCapacityException.class)
    public ResponseEntity<String> over(OverCapacityException overCapacityException){
        return new ResponseEntity<>(overCapacityException.getMessage(),HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> invalidRegister(MethodArgumentNotValidException ex) {
        Map<String, String> error = new HashMap();
        ex.getBindingResult().getFieldErrors().forEach(c -> {
            error.put(c.getField(), c.getDefaultMessage());
        });
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
