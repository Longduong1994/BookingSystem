package booking.controller;

import booking.dto.request.MenuRequest;
import booking.dto.request.ReservationRequest;
import booking.dto.response.MenuResponse;
import booking.dto.response.ReservationResponse;
import booking.dto.response.UserResponseDto;
import booking.exception.*;
import booking.service.impl.menu.IMenuService;
import booking.service.impl.reservation.IReservationService;
import booking.service.impl.user.IUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;


@RestController
@RequestMapping("api/v1/home")
@AllArgsConstructor
public class HomeController {
    private final IReservationService reservationService;
    private final IMenuService menuService;
    @GetMapping("/reservation")
    public ResponseEntity<Page<ReservationResponse>> findReservationByUser(Authentication authentication,
                                                                           @RequestParam(defaultValue = "" ) String code,
                                                                           @RequestParam(defaultValue ="0") int page,
                                                                           @RequestParam(defaultValue ="5") int size) throws LoginException {
        return new ResponseEntity<>(reservationService.findByUser(authentication,code,page,size),HttpStatus.OK);
    }
    @PostMapping("/reservation")
    public ResponseEntity<ReservationResponse> create(@RequestBody @Valid ReservationRequest reservationRequest, Authentication authentication) throws LoginException, NotFoundException, TimeInputException, ParseException, ExistsException, OverCapacityException {
        return new ResponseEntity<>(reservationService.create(reservationRequest,authentication), HttpStatus.OK);
    }

    @PostMapping("reservation/{id}")
    public ResponseEntity<ReservationResponse> create(@RequestBody @Valid ReservationRequest reservationRequest,Authentication authentication,@PathVariable Long id) throws LoginException {
        return new ResponseEntity<>(reservationService.update(reservationRequest,id,authentication), HttpStatus.OK);
    }

    @DeleteMapping("reservation/cancel/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id, Authentication authentication) throws LoginException, OutOfDateException, NotFoundException {
        return new ResponseEntity<>(reservationService.cancel(id,authentication), HttpStatus.OK);
    }

    @GetMapping("/reservation/menu/{id}")
    public ResponseEntity<Page<MenuResponse>> findByReservation(@PathVariable("id") Long id,
                                                                @RequestParam(defaultValue = "")String code,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "5") int size) {
        return new ResponseEntity<>(menuService.findAll(code,id,page,size), HttpStatus.OK);
    }

    @PostMapping("/menu")
    public ResponseEntity<?> addToReservation(@RequestBody MenuRequest menuRequest, Authentication authentication) throws AddMenuException, LoginException, NotFoundException, AccessDeniedException {
        return new ResponseEntity<>(menuService.addToReservation(menuRequest,authentication), HttpStatus.CREATED);
    }

    @DeleteMapping("/menu/{id}/{reservationId}")
    private ResponseEntity<?> removeDish(@PathVariable("id") Long id,Authentication authentication,@PathVariable("reservationId") Long reservationId) throws AccessDeniedException, LoginException, NotFoundException, OutOfDateException {
        return new ResponseEntity<>(menuService.delete(authentication,id,reservationId),HttpStatus.OK);
    }

    @DeleteMapping("/menu/{id}")
    private ResponseEntity<?> clear(@PathVariable("id") Long id,Authentication authentication) throws AccessDeniedException, LoginException, NotFoundException, EmptyException, OutOfDateException {
        return new ResponseEntity<>(menuService.clear(authentication,id),HttpStatus.OK);
    }


}
