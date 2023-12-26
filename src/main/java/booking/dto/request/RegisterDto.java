package booking.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDto {
    @NotEmpty(message = "Not empty ")
    private String username;
    @Pattern(regexp ="^[A-Za-z0-9+_.-]+@(.+)$",message = "Email invalidate")
    private String email;
    @Pattern(regexp ="^(?=.*[A-Za-z])(?=.*\\d).{8,}$",message = "Must be greater than 8 characters with 1 uppercase letter, 1 lowercase letter and 1 number")
    private String password;
    private String confirmPassword;
}
