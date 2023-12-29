package booking.repository;

import booking.dto.response.TableStatusByTime;
import booking.entity.Table;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;


public interface TableRepository extends JpaRepository<Table,Long> {
    Page<Table> findAllByTableNameContaining(String name, Pageable pageable);
    Page<Table> findAllByTableNameContainingAndStatus(String name,boolean status, Pageable pageable);
    boolean existsByTableName(String tableName);

    @Query("SELECT " +
            "    new booking.dto.response.TableStatusByTime(t.tableName, t.capacity,t.description," +
            "    CASE " +
            "        WHEN r.id IS NOT NULL AND ( " +
            "            (r.start IS NULL AND r.end IS NULL) " +
            "            OR (r.start IS NOT NULL AND r.end IS NOT NULL AND r.end >= :start AND r.start <= :end) " +
            "            OR (r.start IS NULL AND r.end IS NOT NULL AND r.end >= :start AND r.start <= :end) " +
            "            OR (r.start IS NOT NULL AND r.end IS NULL AND r.start <= :end) " +
            "        ) THEN 'Has been booked'  " +
            "        ELSE 'Still empty' " +
            "    END) AS status " +
            "FROM Table t " +
            "LEFT JOIN Reservation r ON t.id = r.table.id AND r.bookingDate = :date")
    List<TableStatusByTime> findTableStatusByTime(@Param("start") LocalTime start,
                                               @Param("end") LocalTime end,
                                               @Param("date") Date date);


    @Query("SELECT CASE WHEN COUNT(T) > 0 THEN true ELSE false END " +
            "FROM Table T " +
            "JOIN Reservation R ON R.table.id = T.id " +
            "WHERE T.id = :tableId " +
            "      AND R.bookingDate = :date " +
            "      AND ((R.start BETWEEN :startTime AND :endTime) " +
            "           OR (R.end BETWEEN :startTime AND :endTime))")
    boolean isTableReserved(@Param("tableId") Long tableId,
                            @Param("date") Date date,
                            @Param("startTime") LocalTime startTime,
                            @Param("endTime") LocalTime endTime);

}
