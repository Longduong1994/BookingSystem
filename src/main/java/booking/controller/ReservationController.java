package booking.controller;

import booking.dto.request.ReservationRequest;
import booking.dto.response.ReservationResponse;
import booking.exception.*;
import booking.service.impl.reservation.IReservationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;

@RestController
@RequestMapping("api/v1/admin/reservation")
@AllArgsConstructor
public class ReservationController {
    private final IReservationService reservationService;

    @GetMapping
    public ResponseEntity<Page<ReservationResponse>> findAll(@RequestParam(defaultValue = "") String name,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "5") int size){
        if (date==null){
            date = new Date();
        }
        return new ResponseEntity<>(reservationService.findAll(name,date,page,size), HttpStatus.OK);
    }


    @PutMapping("/confirm/{id}")
    public ResponseEntity<?> confirm(@PathVariable Long id,Authentication authentication) throws OutOfDateException, AccessDeniedException, LoginException, NotFoundException {
        return new ResponseEntity<>(reservationService.confirm(id,authentication),HttpStatus.OK);
    }

    @PutMapping("/completed/{id}")
    public ResponseEntity<?> completed(@PathVariable Long id,Authentication authentication) throws OutOfDateException, AccessDeniedException, LoginException, NotFoundException {
        return new ResponseEntity<>(reservationService.completed(id,authentication),HttpStatus.OK);
    }



}
