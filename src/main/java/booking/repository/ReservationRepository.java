package booking.repository;

import booking.entity.Reservation;
import booking.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    Page<Reservation> findAllByReceiverContainingAndBookingDate(String name, Date date, Pageable pageable);

    Page<Reservation> findAllByUserAndCode(User user, String code, Pageable pageable);

    Page<Reservation> findAllByUser(User user,Pageable pageable);
    Reservation findByCode(String code);
}
