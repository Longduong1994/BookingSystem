package booking.service.impl.reservation;

import booking.dto.request.ReservationRequest;
import booking.dto.response.ReservationResponse;
import booking.entity.*;
import booking.exception.*;
import booking.repository.ReservationRepository;
import booking.repository.TableRepository;
import booking.security.user_principle.UserPrincipal;
import booking.service.impl.mail.MailService;
import booking.service.impl.user.IUserService;
import booking.service.mapper.ReservationMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ReservationService implements IReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final IUserService userService;
    private final TableRepository tableRepository;
    private final MailService mailService;

    @Override
    public Page<ReservationResponse> findAll(String name,Date date, int page, int size) {
        return reservationRepository.findAllByReceiverContainingAndBookingDate(name,date, PageRequest.of(page, size)).map(reservationMapper::toResponse);
    }

    @Override
    public Page<ReservationResponse> findByUser(Authentication authentication, String code, int page, int size) throws LoginException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (userPrincipal == null) {
            throw new LoginException("You must log in to use the service");
        }
        User user = userPrincipal.getUser();
        if (code.equals("")) {
            return reservationRepository.findAllByUser(user, PageRequest.of(page, size)).map(reservationMapper::toResponse);
        } else {
            return reservationRepository.findAllByUserAndCode(user, code, PageRequest.of(page, size)).map(reservationMapper::toResponse);
        }
    }

    @Override
    public ReservationResponse findById(Long id) throws NotFoundException {
        Reservation reservation = reservationRepository.findById(id).get();
        if (reservation == null) {
            throw new NotFoundException("Reservation " + id + " not found");
        }
        return reservationMapper.toResponse(reservation);
    }

    @Override
    public ReservationResponse findByCode(String code) throws NotFoundException {
        Reservation reservation = reservationRepository.findByCode(code);
        if (reservation == null) {
            throw new NotFoundException("Reservation " + code + " not found");
        }
        return reservationMapper.toResponse(reservation);
    }

    @Override
    public ReservationResponse create(ReservationRequest reservationRequest, Authentication authentication) throws LoginException, NotFoundException, TimeInputException, ParseException, ExistsException, OverCapacityException {
        User user = userService.getUser(authentication);
        Table table = tableRepository.findById(reservationRequest.getTableId()).get();
        if (table == null) {
            throw new NotFoundException("Table " + reservationRequest.getTableId() + "not found");
        }
        if (reservationRequest.getPeople()> table.getCapacity()){
            throw new OverCapacityException("The number of people exceeds the capacity of the table" + table.getCapacity());
        }
        if (!reservationRequest.getStart().isBefore(reservationRequest.getEnd())) {
            throw new TimeInputException("The end time must be greater than the start time");
        }
        if (!isTimeValid(reservationRequest.getStart(),reservationRequest.getEnd())){
            throw new TimeInputException("The restaurant starts at 10:00 and ends at 23:00");
        }
        if (tableRepository.isTableReserved(reservationRequest.getTableId(), reservationRequest.getBookingDate(), reservationRequest.getStart(), reservationRequest.getEnd())) {
            throw new ExistsException("The table has been reserved within the above time frame. Please choose another time");
        }
        Reservation reservation = reservationMapper.toEntity(reservationRequest);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        String formattedDate = sdf.format(currentDate);
        Date formattedDateAsDate = sdf.parse(formattedDate);
        reservation.setUser(user);
        reservation.setCode(UUID.randomUUID().toString().substring(0, 6));
        reservation.setTable(table);
        reservation.setCreated(formattedDateAsDate);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservationRepository.save(reservation);
        String content = "Hello " + user.getUsername() + ",\n\n" +
                "Thank you for using our service !!\n"+
                "Your reservation code is: \n"+reservation.getCode()+
                "You can cancel 4 hours in advance !!\n";
        mailService.sendMail(user.getEmail(), "Reservation successfully", content);
        return reservationMapper.toResponse(reservation);
    }

    @Override
    public ReservationResponse update(ReservationRequest reservationRequest, Long id, Authentication authentication) throws LoginException {
       User user = userService.getUser(authentication);
        Reservation reservation = reservationMapper.toEntity(reservationRequest);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public String cancel(Long id, Authentication authentication) throws LoginException, NotFoundException, OutOfDateException {
        User user = userService.getUser(authentication);
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (!reservation.isPresent()) {
            throw new NotFoundException("Reservation not found");
        } else if (!check4HoursPassed(reservation.get().getBookingDate(), reservation.get().getStart())) {
            throw new OutOfDateException("Deadline to cancel");
        } else if (reservation.get().getReservationStatus() == ReservationStatus.COMPLETED) {
            throw new OutOfDateException("Order has been completed");
        }else if (reservation.get().getReservationStatus()== ReservationStatus.CANCEL){
            throw new OutOfDateException("Order has been canceled");
        }
        reservation.get().setReservationStatus(ReservationStatus.CANCEL);
        reservationRepository.save(reservation.get());

        return "Canceled successfully ";
    }

    @Override
    public String confirm(Long id, Authentication authentication) throws LoginException, NotFoundException, OutOfDateException, AccessDeniedException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (userPrincipal == null) {
            throw new LoginException("ou must log in to use the service");
        }
        if (userPrincipal.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"))) {
            Reservation reservation = reservationRepository.findById(id).get();
            if (reservation == null) {
                throw new NotFoundException("Reservation not found");
            }
            if (reservation.getReservationStatus().equals(ReservationStatus.COMPLETED)) {
                throw new OutOfDateException("Reservation has been completed");
            }
            reservation.setReservationStatus(ReservationStatus.CONFIRM);
            reservationRepository.save(reservation);
            return "Confirm successfully";
        }

        throw new AccessDeniedException("You do not have access");
    }

    @Override
    public String completed(Long id, Authentication authentication) throws LoginException, NotFoundException, OutOfDateException, AccessDeniedException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (userPrincipal == null) {
            throw new LoginException("ou must log in to use the service");
        }
        if (userPrincipal.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"))) {
            Reservation reservation = reservationRepository.findById(id).get();
            if (reservation == null) {
                throw new NotFoundException("Reservation not found");
            }
            if (reservation.getReservationStatus().equals(ReservationStatus.COMPLETED)) {
                throw new OutOfDateException("Reservation has been completed");
            }
            reservation.setReservationStatus(ReservationStatus.COMPLETED);
            reservationRepository.save(reservation);
            return "Confirm successfully";
        }

        throw new AccessDeniedException("You do not have access");
    }

    private static boolean check4HoursPassed(Date bookingDate, LocalTime startTime) {
        try {
            java.util.Date utilBookingDate = new java.util.Date(bookingDate.getTime());

            LocalDateTime targetDateTime = utilBookingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime targetDateTimeWithTime = LocalDateTime.of(targetDateTime.toLocalDate(), startTime);
            LocalDateTime currentDateTime = LocalDateTime.now();
            return currentDateTime.isBefore(targetDateTimeWithTime.minusHours(4));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isTimeValid(LocalTime start, LocalTime end) {
        boolean isStartValid = !start.isBefore(LocalTime.parse("10:00"));
        boolean isEndValid = !end.isAfter(LocalTime.parse("23:00"));
        return isStartValid && isEndValid;
    }



}
