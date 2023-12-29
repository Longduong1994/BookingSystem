package booking.controller;

import booking.dto.request.ForgotPasswordDto;
import booking.dto.request.LoginDto;
import booking.dto.request.RegisterDto;
import booking.dto.response.TableResponse;
import booking.dto.response.UserResponse;
import booking.exception.InvalidException;
import booking.exception.LoginException;
import booking.exception.OutOfDateException;
import booking.exception.RegisterException;
import booking.service.impl.dish.IDishService;
import booking.service.impl.table.ITableService;
import booking.service.impl.user.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.Date;
@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;
    private final ITableService tableService;
    private final IDishService dishService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDto registerDto) throws RegisterException {
        return new ResponseEntity<>(userService.register(registerDto), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> handleLogin(@RequestBody LoginDto loginDto) throws LoginException {
        return new ResponseEntity<>(userService.login(loginDto), HttpStatus.OK);
    }

    @GetMapping("/forgotPassword/{email}")
    public ResponseEntity<?> forgotPassword(@PathVariable String email){
        return new ResponseEntity<>(userService.sendVerification(email),HttpStatus.OK);
    }

    @PostMapping("/retrieval")
    public ResponseEntity<?> retrieval(@RequestBody ForgotPasswordDto forgotPasswordDto) throws OutOfDateException, LoginException, InvalidException {
        return new ResponseEntity<>(userService.forgotPassword(forgotPasswordDto),HttpStatus.OK);
    }

    @GetMapping("/tables")
    public ResponseEntity<Page<TableResponse>> findTables(@RequestParam(defaultValue = "") String name,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size) {
        return new ResponseEntity<>(tableService.findAllByStatus(name,page,size),HttpStatus.OK);
    }
    @GetMapping("/tables/status")
    public ResponseEntity<?> getByStatus(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date,
            @RequestParam(defaultValue = "10:00") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime start,
            @RequestParam(defaultValue = "11:00") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime end) {

        if (date == null) {
            date = new Date();
        }
        return new ResponseEntity<>(tableService.findByTime(date, start, end), HttpStatus.OK);
    }

    @GetMapping("/dishes")
    public ResponseEntity<?> findAllByStatus(@RequestParam(defaultValue = "") String name,
                                             @RequestParam(defaultValue = "id") String field,
                                             @RequestParam(defaultValue = "desc") String by,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "5") int size,
                                             @RequestParam(defaultValue = "0") double min,
                                             @RequestParam(defaultValue = "" + Double.MAX_VALUE) double max){
        return new ResponseEntity<>(dishService.findAllByStatus(name,field,by,page,size,min,max), HttpStatus.OK);
    }

    @GetMapping("/dishes/outstanding")
    public ResponseEntity<?> getTopFive(){
        return new ResponseEntity<>(dishService.findTopFive(),HttpStatus.OK);
    }

}
