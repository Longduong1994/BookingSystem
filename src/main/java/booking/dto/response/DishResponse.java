package booking.dto.response;

import booking.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishResponse {
    private Long id;
    private String dishName;
    private String image;
    private String description;
    private String categoryName;
    private boolean status;
}
