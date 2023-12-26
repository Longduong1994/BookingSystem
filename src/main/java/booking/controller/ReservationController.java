package booking.controller;

import booking.dto.request.ReservationRequest;
import booking.dto.response.ReservationResponse;
import booking.exception.LoginException;
import booking.exception.NotFoundException;
import booking.exception.OutOfDateException;
import booking.exception.TimeInputException;
import booking.service.impl.reservation.IReservationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/reservation")
@AllArgsConstructor
public class ReservationController {
    private final IReservationService reservationService;

    @GetMapping
    public ResponseEntity<Page<ReservationResponse>> findAll(@RequestParam(defaultValue = "") String name,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "5") int size){
        return new ResponseEntity<>(reservationService.findAll(name,page,size), HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<ReservationResponse> create(@RequestBody @Valid ReservationRequest reservationRequest,Authentication authentication) throws LoginException, NotFoundException, TimeInputException, ParseException {
        return new ResponseEntity<>(reservationService.create(reservationRequest,authentication), HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ReservationResponse> create(@RequestBody @Valid ReservationRequest reservationRequest,Authentication authentication,@PathVariable Long id) throws LoginException {
        return new ResponseEntity<>(reservationService.update(reservationRequest,id,authentication), HttpStatus.OK);
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id, Authentication authentication) throws LoginException, OutOfDateException, NotFoundException {
        return new ResponseEntity<>(reservationService.cancel(id,authentication), HttpStatus.OK);
    }

}
