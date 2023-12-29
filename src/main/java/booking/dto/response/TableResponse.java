package booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableResponse {
    private Long id;
    private String tableName;
    private int capacity;
    private String description;
    private boolean status;
}
