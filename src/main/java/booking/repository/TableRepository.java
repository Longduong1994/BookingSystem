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
import java.util.Map;
import java.util.Objects;

public interface TableRepository extends JpaRepository<Table,Long> {
    Page<Table> findAllByTableNameContaining(String name, Pageable pageable);

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

}
