package booking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableRequest {
    @NotEmpty(message = "Dish name is not empty ")
    private String tableName;
    private String description;
    @NotNull(message = "Capacity cannot be null")
    @Min(value = 1, message = "Capacity must be greater than or equal to 1")
    private int capacity;
}
