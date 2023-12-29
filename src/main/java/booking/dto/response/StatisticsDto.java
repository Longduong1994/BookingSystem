package booking.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatisticsDto {
    private Long totalOrders;
    private Long completed;
    private Long canceled;
    private double revenue;
    private String bestSellingDish;

    public StatisticsDto(Long totalOrders, Long completed, Long canceled, double revenue, String bestSellingDish) {
        this.totalOrders = totalOrders;
        this.completed = completed;
        this.canceled = canceled;
        this.revenue = revenue;
        this.bestSellingDish = bestSellingDish;
    }
}
