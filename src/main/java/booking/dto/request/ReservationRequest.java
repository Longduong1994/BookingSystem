package booking.dto.request;
import booking.entity.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    @NotNull(message = "The 'people' field cannot be left blank.")
    private Long people;

    @NotNull(message = "The 'table' field cannot be left blank.")
    private Long tableId;

    @NotNull(message = "The 'bookingDate' field cannot be left blank.")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Future(message = "The 'bookingDate' date must be in the future.")
    private Date bookingDate;

    @NotNull(message = "The start time field cannot be left blank.")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime start;

    @NotNull(message = "The end time field cannot be left blank.")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime end;

    @Pattern(regexp = "^0\\d{9}$" ,message = "Invalid phone number")
    private String phone;

    @NotNull(message = "The 'name' field cannot be left blank.")
    private String receiver;
    private String note;
}
