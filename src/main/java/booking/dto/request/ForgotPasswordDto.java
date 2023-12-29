package booking.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordDto {
    @Pattern(regexp ="^[A-Za-z0-9+_.-]+@(.+)$",message = "Email invalidate")
    private String email;
    private String verificationCode;
    @Pattern(regexp ="^(?=.*[A-Za-z])(?=.*\\d).{8,}$",message = "Must be greater than 8 characters with 1 uppercase letter, 1 lowercase letter and 1 number")
    private String newPassword;
    private String confirmNewPassword;
}
