package booking.controller;

import booking.dto.request.ForgotPasswordDto;
import booking.dto.request.LoginDto;
import booking.dto.request.RegisterDto;
import booking.dto.response.UserResponse;
import booking.exception.InvalidException;
import booking.exception.LoginException;
import booking.exception.OutOfDateException;
import booking.exception.RegisterException;
import booking.service.impl.user.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;

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
}
