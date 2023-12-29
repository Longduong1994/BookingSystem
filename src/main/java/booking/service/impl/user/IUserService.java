package booking.service.impl.user;



import booking.dto.request.ChangePasswordDto;
import booking.dto.request.ForgotPasswordDto;
import booking.dto.request.LoginDto;
import booking.dto.request.RegisterDto;
import booking.dto.response.UserResponse;
import booking.dto.response.UserResponseDto;
import booking.entity.User;
import booking.exception.*;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IUserService {
    Page<UserResponseDto> findAll(String username, int page, int size, String field, String by);

    String register(RegisterDto registerDto) throws RegisterException;

    UserResponse login(LoginDto loginDto) throws LoginException;

    String changePassword(ChangePasswordDto changePasswordDto, Authentication authentication) throws LoginException, ExistsException;

    String forgotPassword(ForgotPasswordDto forgotPasswordDto) throws LoginException, InvalidException, OutOfDateException;

    String sendVerification(String email);

    boolean hasAdminRole(User user);
    User getUser(Authentication authentication)throws LoginException;

    String changStatus(Long id) throws NotFoundException;
}
