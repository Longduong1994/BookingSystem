package booking.controller;

import booking.dto.response.UserResponseDto;
import booking.exception.ExistsException;
import booking.service.impl.user.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final IUserService userService;

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> findAll(@RequestParam(defaultValue = "") String username,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "5") int size,
                                                         @RequestParam(defaultValue = "id") String field,
                                                         @RequestParam(defaultValue = "asc") String by) {
        return new ResponseEntity<>(userService.findAll(username, page, size, field, by), HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) throws ExistsException {
        return new ResponseEntity<>(userService.logout(authentication),HttpStatus.OK);
    }
}
