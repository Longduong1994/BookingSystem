package booking.dto.response;

import booking.entity.ReservationStatus;
import booking.entity.Table;
import booking.entity.User;
import jakarta.persistence.*;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private String OrderBy;
    private String code;
    private int people;
    private String tableName;
    private Date bookingDate;
    private Date created;
    private LocalTime start;
    private LocalTime end;
    private String phone;
    private String receiver;
    private String note;
    private String reservationStatus;
}
