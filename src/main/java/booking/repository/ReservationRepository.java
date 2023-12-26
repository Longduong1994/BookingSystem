package booking.repository;

import booking.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    Page<Reservation> findAllByReceiverContaining(String name,Pageable pageable);
    Reservation findByCode(String code);
}
