package booking.service.impl.reservation;

import booking.dto.request.ReservationRequest;
import booking.dto.response.ReservationResponse;
import booking.entity.*;
import booking.exception.*;
import booking.repository.ReservationRepository;
import booking.repository.RoleRepository;
import booking.repository.TableRepository;
import booking.repository.UserRepository;
import booking.security.user_principle.UserPrincipal;
import booking.service.impl.mail.MailService;
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
import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ReservationService implements IReservationService{
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final UserRepository userRepository;
    private final TableRepository tableRepository;
    private final MailService mailService;
    @Override
    public Page<ReservationResponse> findAll(String name, int page, int size) {
        return reservationRepository.findAllByReceiverContaining(name, PageRequest.of(page, size)).map(reservationMapper::toResponse);
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
    public ReservationResponse create(ReservationRequest reservationRequest, Authentication authentication) throws LoginException, NotFoundException, TimeInputException, ParseException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByUsername(userPrincipal.getUsername()).get();
        if (user == null) {
            throw new LoginException("You need to log in to reserve a table");
        }
        Table table = tableRepository.findById(reservationRequest.getTableId()).get();
        if (table == null){
            throw new NotFoundException("Table " + reservationRequest.getTableId() +"not found");
        }
        if (!reservationRequest.getStart().isBefore(reservationRequest.getEnd())){
            throw new TimeInputException("The end time must be greater than the start time");
        }
        Reservation reservation = reservationMapper.toEntity(reservationRequest);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        String formattedDate = sdf.format(currentDate);
        Date formattedDateAsDate = sdf.parse(formattedDate);
        reservation.setUser(user);
        reservation.setCode(UUID.randomUUID().toString().substring(0,6));
        reservation.setTable(table);
        reservation.setCreated(formattedDateAsDate);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservationRepository.save(reservation);
        String email = user.getEmail();
        String content = "Hello " + user.getUsername() + ",\n\n" +
                "Thank you for using our service !!\n" ;
        mailService.sendMail(user.getEmail(), "Active Account", content);
        return reservationMapper.toResponse(reservation);
    }

    @Override
    public ReservationResponse update(ReservationRequest reservationRequest, Long id,Authentication authentication) throws LoginException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByUsername(userPrincipal.getUsername()).get();
        if (user == null) {
            throw new LoginException("You must log in to use the service");
        }
        Reservation reservation = reservationMapper.toEntity(reservationRequest);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public String cancel(Long id,Authentication authentication) throws LoginException, NotFoundException, OutOfDateException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByUsername(userPrincipal.getUsername()).get();
        if (user == null) {
            throw new LoginException("You must log in to use the service");
        }
        Reservation reservation = reservationRepository.findById(id).get();
        if(reservation == null) {
            throw new NotFoundException("Reservation not found");
        }else if(check24HoursPassed(reservation.getCreated(),reservation.getStart()) ){
            throw new OutOfDateException("Deadline to cancel");
        } else if(reservation.getReservationStatus() == ReservationStatus.COMPLETED){
            throw new OutOfDateException("Order has been completed");
        }
        reservation.setReservationStatus(ReservationStatus.CANCEL);
        reservationRepository.save(reservation);

            return "Canceled successfully ";
    }

    @Override
    public String confirm(Long id, Authentication authentication) throws LoginException, NotFoundException, OutOfDateException, AccessDeniedException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if(userPrincipal== null){
            throw new LoginException("ou must log in to use the service");
        }
        if (userPrincipal.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"))){
            Reservation reservation = reservationRepository.findById(id).get();
            if (reservation==null){
                throw new NotFoundException("Reservation not found");
            }
            if (reservation.getReservationStatus().equals(ReservationStatus.COMPLETED)){
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
        if(userPrincipal== null){
            throw new LoginException("ou must log in to use the service");
        }
        if (userPrincipal.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"))){
            Reservation reservation = reservationRepository.findById(id).get();
            if (reservation==null){
                throw new NotFoundException("Reservation not found");
            }
            if (reservation.getReservationStatus().equals(ReservationStatus.COMPLETED)){
                throw new OutOfDateException("Reservation has been completed");
            }
            reservation.setReservationStatus(ReservationStatus.COMPLETED);
            reservationRepository.save(reservation);
            return "Confirm successfully";
        }

        throw new AccessDeniedException("You do not have access");
    }

    private static boolean check24HoursPassed(Date targetDate, LocalTime targetTime) {
        try {
            // Chuyển đổi Date thành LocalDateTime
            LocalDateTime targetDateTime = targetDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

            // Kết hợp targetDateTime và targetTime thành một đối tượng LocalDateTime hoàn chỉnh
            LocalDateTime targetDateTimeWithTime = LocalDateTime.of(targetDateTime.toLocalDate(), targetTime);

            // Lấy thời điểm hiện tại
            LocalDateTime currentDateTime = LocalDateTime.now();

            // Kiểm tra xem đã đủ 24 giờ chưa
            return currentDateTime.isAfter(targetDateTimeWithTime.plusHours(24));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
