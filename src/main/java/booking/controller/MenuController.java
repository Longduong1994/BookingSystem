package booking.controller;

import booking.dto.request.MenuRequest;
import booking.dto.response.MenuResponse;
import booking.exception.AddMenuException;
import booking.exception.LoginException;
import booking.exception.NotFoundException;
import booking.service.impl.menu.IMenuService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/menus")
@AllArgsConstructor
public class MenuController {
    private final IMenuService menuService;

    @GetMapping
    public ResponseEntity<Page<MenuResponse>> findByReservation(@RequestParam(required = false) Long id,
                                                                @RequestParam(defaultValue = "")String code,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "5") int size) {
        return new ResponseEntity<>(menuService.findAll(code,id,page,size), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addToReservation(@RequestBody MenuRequest menuRequest, Authentication authentication) throws AddMenuException, LoginException, NotFoundException {
        return new ResponseEntity<>(menuService.addToReservation(menuRequest,authentication), HttpStatus.CREATED);
    }
}
