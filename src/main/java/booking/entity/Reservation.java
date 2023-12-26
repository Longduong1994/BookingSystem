package booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.util.Date;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private String code;
    private int people;
    @ManyToOne(fetch = FetchType.LAZY)
    private Table table;
    @Column(columnDefinition = "DATE")
    @Temporal(TemporalType.DATE)
    private Date created;
    @Column(columnDefinition = "DATE")
    @Temporal(TemporalType.DATE)
    private Date bookingDate;
    @Column(columnDefinition = "TIME")
    private LocalTime start;
    @Column(columnDefinition = "TIME")
    private LocalTime end;
    private String phone;
    private String receiver;
    private String note;
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;
}
