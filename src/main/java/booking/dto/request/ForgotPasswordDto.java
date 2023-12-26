package booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordDto {

    private String email;
    private String verificationCode;
    private String newPassword;
    private String confirmNewPassword;
}
