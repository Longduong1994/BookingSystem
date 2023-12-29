package booking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuRequest {
    @NotNull(message = "The 'dish id' field cannot be left blank.")
    private Long dishId;
    @NotNull(message = "The 'reservation id' field cannot be left blank.")
    private Long reservationId;
    @Min(value = 1, message = "Quantity must be greater than 0.")
    private int quantity = 1;
}
