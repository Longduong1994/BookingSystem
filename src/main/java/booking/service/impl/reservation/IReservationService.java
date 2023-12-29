package booking.service.impl.reservation;

import booking.dto.request.ReservationRequest;
import booking.dto.response.ReservationResponse;
import booking.exception.*;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.text.ParseException;
import java.util.Date;

public interface IReservationService {

    Page<ReservationResponse> findAll(String name, Date date, int page, int size);
    Page<ReservationResponse> findByUser(Authentication authentication,String code,int page,int size) throws LoginException;

    ReservationResponse findById(Long id) throws NotFoundException;
    ReservationResponse findByCode(String code) throws NotFoundException;

    ReservationResponse create(ReservationRequest reservationRequest, Authentication authentication) throws LoginException, NotFoundException, TimeInputException, ParseException, ExistsException, OverCapacityException;

    ReservationResponse update(ReservationRequest reservationRequest,Long id,Authentication authentication) throws LoginException;

    String cancel(Long id,Authentication authentication) throws LoginException, NotFoundException, OutOfDateException;
    String confirm(Long id,Authentication authentication) throws LoginException, NotFoundException, OutOfDateException, AccessDeniedException;
    String completed(Long id,Authentication authentication) throws LoginException, NotFoundException, OutOfDateException, AccessDeniedException;
}
