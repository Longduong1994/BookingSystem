package booking.repository;

import booking.dto.response.StatisticsDto;
import booking.entity.Menu;
import booking.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu,Long> {
    Page<Menu> findAllByReservationCodeOrReservationId(String code,Long id,Pageable pageable);
    List<Menu> findAllByReservationId(Long id);

    @Modifying
    @Query("DELETE FROM Menu M WHERE M.reservation = :reservation")
    void deleteAllByReservation(@Param("reservation") Reservation reservation);

    @Query("SELECT SUM(M.price*M.quantityDelivered) FROM Menu M JOIN Reservation R ON M.reservation=R WHERE R.reservationStatus = 'COMPLETED' AND R.bookingDate=:date ")
    double revenueByDate(@Param("date") Date date);

    @Query("SELECT new booking.dto.response.StatisticsDto(" +
            "COALESCE(COUNT(R), 0), " +
            "COALESCE(SUM(CASE WHEN R.reservationStatus = 'COMPLETED' THEN 1 ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN R.reservationStatus = 'CANCEL' THEN 1 ELSE 0 END), 0), " +
            "COALESCE(SUM(M.quantityDelivered * M.price), 0), " +
            "COALESCE((SELECT M2.dish.dishName FROM Menu M2 " +
            " WHERE M2.reservation.bookingDate = :date " +
            " GROUP BY M2.dish.dishName " +
            " ORDER BY COUNT(M2) DESC " +
            " LIMIT 1), 'not yet')) " +
            "FROM Menu M JOIN M.reservation R " +
            "WHERE R.bookingDate = :date")
    StatisticsDto getStatisticsByDate(@Param("date") Date date);

    @Query("SELECT coalesce( SUM(M.quantityDelivered * M.price),0)  FROM Menu M JOIN Reservation R ON M.reservation=R WHERE R=:reservation")
    double totalReservation(Reservation reservation);


}
