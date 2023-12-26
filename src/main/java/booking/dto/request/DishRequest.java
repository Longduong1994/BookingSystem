package booking.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishRequest {
    @NotEmpty(message = "Dish name is not empty ")
    private String dishName;
    @NotNull(message = "Image is not empty")
    private MultipartFile file;
    private String description;
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private double price;
    @NotNull(message = "Not empty")
    private Long categoryId;
}
