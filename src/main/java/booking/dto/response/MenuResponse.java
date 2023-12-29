package booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {
    private Long id;
    private String dishName;
    private String image;
    private double price;
    private int quantityOrdered;
    private int quantityDelivered;

}
