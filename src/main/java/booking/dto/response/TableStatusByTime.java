package booking.dto.response;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class TableStatusByTime {
    private String tableName;
    private int capacity;
    private String description;
    private String status;

    public TableStatusByTime(String tableName, int capacity, String description, String status) {
        this.tableName = tableName;
        this.capacity = capacity;
        this.description = description;
        this.status = status;
    }
}
