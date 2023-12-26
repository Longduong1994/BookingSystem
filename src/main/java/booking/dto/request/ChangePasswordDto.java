package booking.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordDto {
    @Pattern(regexp ="^(?=.*[A-Za-z])(?=.*\\d).{8,}$",message = "Must be greater than 8 characters with 1 uppercase letter, 1 lowercase letter and 1 number")
    private String oldPassword;
    @Pattern(regexp ="^(?=.*[A-Za-z])(?=.*\\d).{8,}$",message = "Must be greater than 8 characters with 1 uppercase letter, 1 lowercase letter and 1 number")
    private String newPassword;
    private String confirmNewPassword;
}
